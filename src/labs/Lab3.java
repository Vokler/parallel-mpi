package labs;

import mpi.MPI;
import mpi.Status;
import utils.Utils;

import java.util.Arrays;

public class Lab3 {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int count, TAG = 0;
        Status st;

        int N = 3;
        int threadsCount = size - 1;
        int k = N / threadsCount + (N % threadsCount != 0 ? 1 : 0);
        int[] vector = {1, 2, 3};

        if (rank == 0) {
//            int[][] matrix = new int[N][N];
            int[][] matrix = {
                    {1, 2, 3},
                    {4, 5, 6},
                    {7, 8, 9}
            };


//            for (int i = 0; i < N; i++) {
//                for (int j = 0; j < N; j++) {
//                    matrix[i][j] = Utils.randomInt(1, 10);
//                }
//            }

            System.out.println("Matrix:");
            Utils.printMatrix(matrix);
            System.out.println("Vector: " + Arrays.toString(vector));

            for (int i_rank = 1; i_rank <= threadsCount; i_rank++) {
                int start = Utils.getStartIndex(i_rank, k);
                int step = Utils.getStep(start, k, N);

                for (int j = start; j < start + step; j++) {
                    int[] arr = new int[N];
                    for (int i = 0; i < N; i++) {
                        arr[i] = matrix[i][j];
                    }
                    MPI.COMM_WORLD.Send(arr, 0, arr.length, MPI.INT, i_rank, TAG);
                }
            }

            int[][] resultMatrix = new int[N][N];
            for (int i_rank = 1; i_rank <= threadsCount; i_rank++) {
                int[] message = new int[N];
                MPI.COMM_WORLD.Recv(message, 0, N, MPI.INT, i_rank, TAG);
                for (int i = 0; i < N; i++) {
//                    int[] message = new int[N];
                    for (int j = 0; j < N; j++) {
                        MPI.COMM_WORLD.Recv(message, 0, N, MPI.INT, i_rank, TAG);
                    }
                    System.out.println(Arrays.toString(message));
                }
            }

        } else {
            st = MPI.COMM_WORLD.Probe(0, TAG);
            count = st.Get_count(MPI.INT);

            for (int i = 0; i < k; i++) {
                int[] messageRecv = new int[count];
                MPI.COMM_WORLD.Recv(messageRecv, 0, count, MPI.INT, 0, TAG);
                for (int j = 0; j < count; j++) {
                    messageRecv[j] *= vector[j];
                }
                MPI.COMM_WORLD.Isend(messageRecv, 0, count, MPI.INT, 0, TAG);
            }


        }

        MPI.Finalize();
    }
}
