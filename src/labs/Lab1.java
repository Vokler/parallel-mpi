package labs;

import mpi.MPI;
import mpi.Request;
import mpi.Status;
import utils.Utils;

import java.util.Arrays;

public class Lab1 {
    public static void main(String[] args) {
        int N = 14;
        int[] vector = new int[N];
        int num = 2;

        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int threadsCount = size - 1;
        Request[] reqs = new Request[threadsCount];
        int k = vector.length / threadsCount + (vector.length % threadsCount != 0 ? 1 : 0);
        int count, TAG = 0;
        Status st;

        if (rank == 0) {

            for (int i = 0; i < N; i++) {
                vector[i] = Utils.randomInt(1, 10);
            }

            System.out.println("Vector: " + Arrays.toString(vector));
            System.out.println("Number: " + num);
            for (int i = 1; i <= threadsCount; i++) {
                int start = Utils.getStartIndex(i, k);
                int countToSend = Utils.getStep(start, k, N);
                MPI.COMM_WORLD.Isend(vector, start, countToSend, MPI.INT, i, TAG);
            }
            for (int i = 1; i <= threadsCount; i++) {
                int start = Utils.getStartIndex(i, k);
                int countToRecv = Utils.getStep(start, k, N);
                reqs[i - 1] = MPI.COMM_WORLD.Irecv(vector, start, countToRecv, MPI.INT, i, TAG);
            }
            Request.Waitall(reqs);
            System.out.println("Result: " + Arrays.toString(vector));
        } else {
            st = MPI.COMM_WORLD.Probe(0, TAG);
            count = st.Get_count(MPI.INT);
            int[] messageRecv = new int[count];
            Request req = MPI.COMM_WORLD.Irecv(messageRecv, 0, count, MPI.INT, 0, TAG);
            req.Wait();
            for (int i = 0; i < count; i++) {
                messageRecv[i] *= num;
            }
            MPI.COMM_WORLD.Isend(messageRecv, 0, count, MPI.INT, 0, TAG);
        }

        MPI.Finalize();
    }
}
