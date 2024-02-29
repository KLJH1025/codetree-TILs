import java.io.*;
import java.util.*;

public class Main {

    static class Point {
        int y, x;
        Point(int y, int x) {
            this.y = y;
            this.x = x;
        }
    }

    static int N, M, T;
    static int[][] map, copyMap;
    static int[] dy = {-1, 0, 1, 0};
    static int[] dx = {0, 1, 0, - 1};
    static ArrayList<Point> blast;

    // start: 19:30
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        T = Integer.parseInt(st.nextToken());

        map = new int[N][M];
        blast = new ArrayList<>();

        for(int i=0; i<N; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<M; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());

                if(map[i][j] == -1) {
                    blast.add(new Point(i, j));
                }
            }
        }

        for(int t=0; t<T; t++) {
            // 복사 맵 초기화
            copyMap = new int[N][M];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    copyMap[i][j] = 0;
                }
            }

            // 먼지 확산
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    dustSpread(i, j);
                }
            }

            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    map[i][j] += copyMap[i][j];
                }
            }

            // 먼지 이동
            upWind();
            downWind();
        }

//        for(int i=0; i<N; i++) {
//            for(int j=0; j<M; j++) {
//                System.out.print(map[i][j] + " ");
//            }
//            System.out.println();
//        }

        // 정답 출력
        System.out.println(countDustAnswer());
    }

    // 위쪽 바람
    public static void upWind() {
        Point upBlast = blast.get(0);
        int upBlastY = upBlast.y;

        // V
        for(int i=upBlastY; i>0; i--) {
            map[i][0] = map[i-1][0];
        }

        // <
        for(int i=0; i<M-1; i++) {
            map[0][i] = map[0][i+1];
        }

        // ^
        for(int i=0; i<upBlastY; i++) {
            map[i][M-1] = map[i+1][M-1];
        }

        // >
        for(int i=M-1; i>0; i--) {
            map[upBlastY][i] = map[upBlastY][i-1];
        }

        map[upBlastY][1] = 0;
        map[upBlastY][0] = -1;
    }

    // 아래쪽 바람
    public static void downWind() {
        Point downBlast = blast.get(1);
        int downBlastY = downBlast.y;

        // ^
        for(int i=downBlastY; i<N-1; i++) {
            map[i][0] = map[i+1][0];
        }

        // <
        for(int i=0; i<M-1; i++) {
            map[N-1][i] = map[N-1][i+1];
        }

        // V
        for(int i=N-1; i>downBlastY; i--) {
            map[i][M - 1] = map[i - 1][M - 1];
        }

        // >
        for(int i=M-1; i>0; i--) {
            map[downBlastY][i] = map[downBlastY][i-1];
        }

        map[downBlastY][1] = 0;
        map[downBlastY][0] = -1;
    }

    // 먼지 확산
    public static void dustSpread(int y, int x) {
        int spreadAmount = map[y][x] / 5;

        for(int i=0; i<4; i++) {
            int nextY = y + dy[i];
            int nextX = x + dx[i];

            if(!check(nextY, nextX)) continue;
            if(map[nextY][nextX] == -1) continue;

            copyMap[nextY][nextX] += spreadAmount;
            copyMap[y][x] -= spreadAmount;
        }
    }

    // 시공의 돌풍의 청소



    public static boolean check(int y, int x) {
        if(y < 0 || x < 0 || x >= M || y >= N) {
            return false;
        }
        return true;
    }

    public static int countDustAnswer() {
        int count = 0;
        for(int i=0; i<N; i++) {
            for (int j = 0; j < M; j++) {
                if (map[i][j] == -1) continue;

                count += map[i][j];
            }
        }
        return count;
    }
}