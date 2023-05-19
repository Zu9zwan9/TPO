#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>

#define NRA 62                 /* number of rows in matrix A */
#define NCA 15                 /* number of columns in matrix A */
#define NCB 7                  /* number of columns in matrix B */
#define MASTER 0               /* taskid of first task */
#define FROM_MASTER 1          /* setting a message type */
#define FROM_WORKER 2          /* setting a message type */

void printMatrix(double matrix[][NCB], int rows, int columns) {
    int i, j;
    for (i = 0; i < rows; i++) {
        for (j = 0; j < columns; j++)
            printf("%6.2f   ", matrix[i][j]);
        printf("\n");
    }
}

int main(int argc, char *argv[]) {
    int numtasks, taskid, numworkers, source, dest, rows, offset, i, j, k;
    double a[NRA][NCA], b[NCA][NCB], c[NRA][NCB];
    MPI_Status status;
    MPI_Request req_recv_offset, req_recv_rows, req_recv_a, req_recv_b, req_recv_c, req_send_offset, req_send_rows, req_send_a, req_send_b, req_send_c;

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &taskid);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    numworkers = numtasks - 1;

    if (numtasks < 2) {
        printf("Need at least two MPI tasks. Quitting...\n");
        MPI_Abort(MPI_COMM_WORLD, 1);
        exit(1);
    }

    if (taskid == MASTER) {
        printf("Number of MPI tasks is: %d\n", numtasks);
        printf("Initializing arrays...\n");
        for (i = 0; i < NRA; i++)
            for (j = 0; j < NCA; j++)
                a[i][j] = i + j;

        for (i = 0; i < NCA; i++)
            for (j = 0; j < NCB; j++)
                b[i][j] = i * j;

        // Send matrix data to the worker tasks
        rows = NRA / numworkers;
        offset = 0;

        for (dest = 1; dest <= numworkers; dest++) {
            MPI_Isend(&offset, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD, &req_send_offset);
            MPI_Isend(&rows, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD, &req_send_rows);
            MPI_Isend(&a[offset][0], rows * NCA, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD, &req_send_a);
            MPI_Isend(&b, NCA * NCB, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD, &req_send_b);

            offset += rows;
        }

        // Receive results from worker tasks
        for (source = 1; source <= numworkers; source++) {
            MPI_Recv(&offset, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &status);
            MPI_Recv(&rows, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &status);
            MPI_Recv(&c[offset][0], rows * NCB, MPI_DOUBLE, source, FROM_WORKER, MPI_COMM_WORLD, &status);
        }

        printf("Result Matrix:\n");
        printMatrix(c, NRA, NCB);
    }

    if (taskid > MASTER) {
        // Receive matrix data from the master non-blocking messaging methods
        MPI_Recv(&offset, 1, MPI_INT, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        MPI_Recv(&rows, 1, MPI_INT, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        MPI_Recv(&a, rows * NCA, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        MPI_Recv(&b, NCA * NCB, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);

        // Perform matrix multiplication
        for (k = 0; k < NCB; k++) {
            for (i = 0; i < rows; i++) {
                c[i][k] = 0.0;
                for (j = 0; j < NCA; j++) {
                    c[i][k] += a[i][j] * b[j][k];
                }
            }
        }

        // Send the results back to the master non-blocking messaging methods are used
        MPI_Isend(&offset, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD, &req_send_offset);
        MPI_Isend(&rows, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD, &req_send_rows);
        MPI_Isend(&c, rows * NCB, MPI_DOUBLE, MASTER, FROM_WORKER, MPI_COMM_WORLD, &req_send_c);

        // Wait for all non-blocking sends and receives to complete
        MPI_Wait(&req_send_offset, &status);
        MPI_Wait(&req_send_rows, &status);
        MPI_Wait(&req_send_c, &status);
    }

    MPI_Finalize();
    return 0;
}

