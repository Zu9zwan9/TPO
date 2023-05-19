#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define NRA 62                 /* number of rows in matrix A */
#define NCA 15                 /* number of columns in matrix A */
#define NCB 7                  /* number of columns in matrix B */
#define MASTER 0               /* taskid of first task */
#define FROM_MASTER 1          /* setting a message type */
#define FROM_WORKER 2          /* setting a message type */

void printMatrix(double *matrix, int rows, int columns) {
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
            printf("%f ", matrix[i * columns + j]);
        }
        printf("\n");
    }
}

void comparePerformance(int matrixSize, int numNodes) {
    int numtasks, taskid, numworkers, source, dest, rows, offset, i, j, k;
    double a[matrixSize][matrixSize], b[matrixSize][matrixSize], c[matrixSize][matrixSize];
    MPI_Status status;

    MPI_Init(NULL, NULL);
    MPI_Comm_rank(MPI_COMM_WORLD, &taskid);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    numworkers = numtasks - 1;

    if (numtasks < 2) {
        printf("Need at least two MPI tasks. Quitting...\n");
        MPI_Abort(MPI_COMM_WORLD, 1);
        exit(1);
    }

    if (taskid == MASTER) {
        printf("Number of MPI tasks: %d\n", numtasks);
        printf("Matrix size: %d x %d\n", matrixSize, matrixSize);
        printf("Initializing arrays...\n");

        srand(time(NULL)); // Initialize random number generator

        // Initialize matrix A with random values
        for (i = 0; i < matrixSize; i++)
            for (j = 0; j < matrixSize; j++)
                a[i][j] = (double) rand() / RAND_MAX;

        // Initialize matrix B with random values
        for (i = 0; i < matrixSize; i++)
            for (j = 0; j < matrixSize; j++)
                b[i][j] = (double) rand() / RAND_MAX;

        // Send matrix data to the worker tasks
        rows = matrixSize / numworkers;
        offset = 0;

        for (dest = 1; dest <= numworkers; dest++) {
            MPI_Send(&offset, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD);
            MPI_Send(&rows, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD);
            MPI_Send(&a[offset][0], rows * matrixSize, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD);
            MPI_Send(&b, matrixSize * matrixSize, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD);

            offset += rows;
        }

        // Receive results from worker tasks
        for (source = 1; source <= numworkers; source++) {

            MPI_Recv(&offset, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &status);
            MPI_Recv(&rows, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &status);
            MPI_Recv(&c[offset][0], rows * matrixSize, MPI_DOUBLE, source,
                     FROM_WORKER, MPI_COMM_WORLD, &status);
        }
        // Print results
        printf("******************************************************\n");
        printf("Result Matrix:\n");
        printMatrix(&c[0][0], matrixSize, matrixSize);
        printf("******************************************************\n");

        printf("Done.\n");

    } else if (taskid > MASTER) {
        // Receive data from the master task
        MPI_Recv(&offset, 1, MPI_INT, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        MPI_Recv(&rows, 1, MPI_INT, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        MPI_Recv(&a, rows * matrixSize, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        MPI_Recv(&b, matrixSize * matrixSize, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        int rank;
        MPI_Comm_rank(MPI_COMM_WORLD, &rank);

        // Perform matrix multiplication
        for (k = 0; k < matrixSize; k++) {
            for (i = 0; i < rows; i++) {
                c[i][k] = 0.0;
                for (j = 0; j < matrixSize; j++) {
                    c[i][k] += a[i][j] * b[j][k];
                }
            }
        }

        // Send results back to the master task
        MPI_Send(&offset, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD);
        MPI_Send(&rows, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD);
        MPI_Send(&c, rows * matrixSize, MPI_DOUBLE, MASTER, FROM_WORKER, MPI_COMM_WORLD);
    }

    MPI_Finalize();
}

int main() {
    int matrixSize = NRA;  // Set the matrix size
    int numNodes = 4;     // Set the number of nodes

    comparePerformance(matrixSize, numNodes);

    return 0;
}
