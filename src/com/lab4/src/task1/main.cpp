#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#define NRA 62                 /* number of rows in matrix A */
#define NCA 15                 /* number of columns in matrix A */
#define NCB 7                  /* number of columns in matrix B */
#define MASTER 0               /* taskid of first task */
#define FROM_MASTER 1          /* setting a message type */
#define FROM_WORKER 2          /* setting a message type */

int main(int argc, char *argv[]) {
    int numtasks,              /* number of tasks in partition */
    taskid,                /* a task identifier */
    numworkers,            /* number of worker tasks */
    source,                /* task id of message source */
    dest,                  /* task id of message destination */
    mtype,                 /* message type */
    rows,                  /* rows of matrix A sent to each worker */
    averow, extra, offset, /* used to determine rows sent to each worker */
    i, j, k, rc;           /* misc */
    double a[NRA][NCA],           /* matrix A to be multiplied */
    b[NCA][NCB],           /* matrix B to be multiplied */
    c[NRA][NCB];           /* result matrix C */
    MPI_Status status;

    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &taskid);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    if (numtasks < 2) {
        printf("Need at least two MPI tasks. Quitting...\n");
        MPI_Abort(MPI_COMM_WORLD, rc);
        exit(1);
    }
    numworkers = numtasks - 1;

    /**************************** master task ************************************/
    if (taskid == MASTER) {
        printf("mpi_mm has started with %d tasks.\n", numtasks);
        printf("Initializing arrays...\n");

        /* Initialize matrix A with random values */
        for (i = 0; i < NRA; i++) {
            for (j = 0; j < NCA; j++) {
                a[i][j] = (double)rand() / RAND_MAX; // Random value between 0 and 1
            }
        }

        /* Initialize matrix B with random values */
        for (i = 0; i < NCA; i++) {
            for (j = 0; j < NCB; j++) {
                b[i][j] = (double)rand() / RAND_MAX; // Random value between 0 and 1
            }
        }

        /* Send matrix data to the worker tasks */
        averow = NRA / numworkers;
        extra = NRA % numworkers;
        offset = 0;
        mtype = FROM_MASTER;
        for (dest = 1; dest <= numworkers; dest++) {
            rows = (dest <= extra) ? averow + 1 : averow;
            printf("Sending %d rows to task %d offset=%d\n", rows, dest, offset);
            MPI_Send(&offset, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD);
            MPI_Send(&rows, 1, MPI_INT, dest, FROM_MASTER, MPI_COMM_WORLD);
            MPI_Send(&a[offset][0], rows * NCA, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD);
            MPI_Send(&b, NCA * NCB, MPI_DOUBLE, dest, FROM_MASTER, MPI_COMM_WORLD);
            offset = offset + rows;
        }

        /* Receive results from worker tasks */
        for (i = 1; i <= numworkers; i++) {
            source = i;
            MPI_Recv(&offset, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &status);
            MPI_Recv(&rows, 1, MPI_INT, source, FROM_WORKER, MPI_COMM_WORLD, &status);
            MPI_Recv(&c[offset][0], rows * NCB, MPI_DOUBLE, source, FROM_WORKER, MPI_COMM_WORLD, &status);
            printf("Received results from task %d\n", source);
        }

        /* Print results */
        printf("******************************************************\n");
        printf("Result Matrix:\n");
        for (i = 0; i < NRA; i++) {
            printf("\n");
            for (j = 0; j < NCB; j++)
                printf("%6.2f ", c[i][j]);
        }
        printf("\n******************************************************\n");
        printf("Done.\n");
    }

    /**************************** worker task ************************************/
    if (taskid > MASTER) {
        MPI_Recv(&offset, 1, MPI_INT, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        MPI_Recv(&rows, 1, MPI_INT, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        MPI_Recv(&a, rows * NCA, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);
        MPI_Recv(&b, NCA * NCB, MPI_DOUBLE, MASTER, FROM_MASTER, MPI_COMM_WORLD, &status);

        /* Perform matrix multiplication */
        for (k = 0; k < NCB; k++) {
            for (i = 0; i < rows; i++) {
                c[i][k] = 0.0;
                for (j = 0; j < NCA; j++)
                    c[i][k] = c[i][k] + a[i][j] * b[j][k];
            }
        }

        MPI_Send(&offset, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD);
        MPI_Send(&rows, 1, MPI_INT, MASTER, FROM_WORKER, MPI_COMM_WORLD);
        MPI_Send(&c, rows * NCB, MPI_DOUBLE, MASTER, FROM_WORKER, MPI_COMM_WORLD);
    }


    MPI_Finalize();

}
/*
The code begins by including the necessary header files and defining some constants and variables.

MPI is initialized using MPI_Init function, and the task ID and total number of tasks are obtained using MPI_Comm_rank and MPI_Comm_size respectively.

The code checks if the number of tasks is at least 2. If not, it prints an error message and aborts the MPI execution.

The master task (task with ID 0) and worker tasks (all other tasks) follow different execution paths.

In the master task's execution path, the code initializes two matrices, A and B, with random values. Matrix A has dimensions NRA x NCA, and matrix B has dimensions NCA x NCB.

The master task sends portions of matrix A and the entire matrix B to the worker tasks using MPI_Send. It determines the number of rows each worker will receive based on the total number of rows (NRA), the number of workers (numworkers), and the row offset.

After sending the data, the master task receives the computed portions of matrix C from each worker using MPI_Recv.

The master task then prints the result matrix C.

In the worker task's execution path, each worker receives the row offset, the number of rows to process, and the data of matrices A and B using MPI_Recv.

Each worker performs the matrix multiplication by iterating over the assigned rows and columns, and stores the results in a local portion of matrix C.

The worker task sends the computed portion of matrix C back to the master task using MPI_Send.

Finally, MPI is finalized using MPI_Finalize, and the program ends. */