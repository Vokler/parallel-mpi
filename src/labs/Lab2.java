package labs;

import mpi.MPI;
import mpi.Request;
import mpi.Status;
import utils.Utils;

import java.util.Arrays;

public class Lab2 {

    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int count, TAG = 0;
        Status st;

        int N = 100;

        int threadsCount = size - 1;
        Request[] reqs = new Request[threadsCount];
        int k = N / threadsCount + (N % threadsCount != 0 ? 1 : 0);

        if (rank == 0) {
            int[] firstVector = new int[N];
            int[] secondVector = new int[N];

            for (int i = 0; i < N; i++) {
                firstVector[i] = Utils.randomInt(1, 10);
                secondVector[i] = Utils.randomInt(1, 10);
            }

            System.out.println("First vector: " + Arrays.toString(firstVector));
            System.out.println("Second vector: " + Arrays.toString(secondVector));

            for (int i = 1; i <= threadsCount; i++) {
                int start = Utils.getStartIndex(i, k);
                int step = Utils.getStep(start, k, N);
                MPI.COMM_WORLD.Isend(firstVector, start, step, MPI.INT, i, TAG);
                MPI.COMM_WORLD.Isend(secondVector, start, step, MPI.INT, i, TAG);
            }

            int[] messageRecv = new int[threadsCount];
            for (int i = 1; i <= threadsCount; i++) {
                int index = i - 1;
                reqs[index] = MPI.COMM_WORLD.Irecv(messageRecv, index, 1, MPI.INT, i, TAG);
            }

            Request.Waitall(reqs);

            int result = 0;
            for (int i : messageRecv) {
                result += i;
            }
            System.out.println("Result: " + result);

        } else {
            st = MPI.COMM_WORLD.Probe(0, TAG);
            count = st.Get_count(MPI.INT);
            int[] firstMessageRecv = new int[count];
            int[] secondMessageRecv = new int[count];

            Request req1 = MPI.COMM_WORLD.Irecv(firstMessageRecv, 0, count, MPI.INT, 0, TAG);
            req1.Wait();

            Request req2 = MPI.COMM_WORLD.Irecv(secondMessageRecv, 0, count, MPI.INT, 0, TAG);
            req2.Wait();

            int[] result = {0};
            for (int i = 0; i < count; i++) {
                result[0] += firstMessageRecv[i] * secondMessageRecv[i];
            }
            MPI.COMM_WORLD.Isend(result, 0, 1, MPI.INT, 0, TAG);
        }

        MPI.Finalize();
    }
}
