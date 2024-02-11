import java.util.*;
import java.io.*;

public class Main {

    static class Point {
        int y, x, color;

        Point(int y, int x, int color) {
            this.y = y;
            this.x = x;
            this.color = color;
        }
    }

    static class Group implements Comparable<Group> {
        int maxRow = -1;
        int minCol = 100;
        int containsRed = 0;
        ArrayList<Point> points;

        Group(ArrayList<Point> points) {
            this.points = points;

            for(Point p : points) {
                if(p.color != 0 || p.color != -2) {
                    maxRow = Math.max(maxRow, p.y);
                    minCol = Math.min(minCol, p.x);
                }
                else if(p.color == 0){
                    containsRed += 1;
                }
            }
        }

        @Override
        public int compareTo(Group g) {
            if(this.points.size() == g.points.size()) {
                if(this.containsRed == g.containsRed) {
                    if(this.maxRow == g.maxRow) {
                        return this.minCol - g.minCol;
                    }
                    return g.maxRow - this.maxRow;
                }
                return g.containsRed - this.containsRed;
            }
            return g.points.size() - this.points.size();
        }
    }

    static int[] dy = {-1, 0, 1, 0};
    static int[] dx = {0, 1, 0, -1};
    static int n, m;
    static int[][] map;
    static boolean[][] visited;
    static PriorityQueue<Group> pq;
    // 3시 10분
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        map = new int[n][n];
        for(int i=0; i<n; i++) {
            st = new StringTokenizer(br.readLine());
            for(int j=0; j<n; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // -1: 검은색 돌, 0: 빨간색 폭탄, 1<=x<=m: 빨간색과는 서로 다른 색의 폭탄
        int score = 0;
        while(true) {
            // 폭탄 묶음: 같은색으로만 2개이상 (빨강X) or 같은색+빨간색(2개이상) // 빨간색만으로는 불가
            pq = new PriorityQueue<>();

            visited = new boolean[n][n];
            for(int r=0; r<n; r++) {
                for(int c=0; c<n; c++) {
                    findGroup(r, c, map[r][c]);
                }
            }

            if(pq.isEmpty()) break;

            // 폭탄 고르는 우선순위
            // 같은 크기 중에서 빨간색 폭탄이 적은 것 > 빨간색이 아니면서 행이 젤 큰 칸 > 열이 가장 작은 칸
            Group group = pq.poll();
            //
            for(Point p : group.points) {
                // System.out.println("p.y: " + p.y + ", p.x: " + p.x);
            }
            
            //
            for(Point p : group.points) {
                map[p.y][p.x] = -2;
            }
            score += (group.points.size() * group.points.size());
            // System.out.println("score: " + score);
            // System.out.println("===========");

            // printMap();
            // 선택된 폭탄 제거 후에 중력이 작용하여 밑으로 내려감. 돌들은 위치 고정
            gravity();
            // printMap();
            // 반시계방향 90도 회전
            rotateMap();
            // printMap();
            // 중력 작용하여 폭탄들 밑으로 내려감. 돌들은 위치 고정
            gravity();
            // printMap();
            //System.out.println("========================");
        }
        System.out.println(score);
    }

    public static void findGroup(int y, int x, int standardColor) {
        if(standardColor == -1 || standardColor == -2) {
            visited[y][x] = true;
            return;
        }

        if(standardColor == 0) {
            return;
        }

        ArrayList<Point> arr = new ArrayList<>();

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(y, x, map[y][x]));
        arr.add(new Point(y, x, map[y][x]));
        visited[y][x] = true;

        while(!queue.isEmpty()) {
            Point p = queue.poll();

            for(int k=0; k<4; k++) {
                int nextY = p.y + dy[k];
                int nextX = p.x + dx[k];

                if(!check(nextY, nextX)) continue;

                if(visited[nextY][nextX]) continue;

                if(map[nextY][nextX] == standardColor || map[nextY][nextX] == 0) {
                    visited[nextY][nextX] = true;
                    queue.add(new Point(nextY, nextX, map[nextY][nextX]));
                    arr.add(new Point(nextY, nextX, map[nextY][nextX]));
                }
            }
        }

        for(Point p : arr) {
            if(p.color == 0) {
                visited[p.y][p.x] = false;
            }
        }

        if(arr.size() <= 1) return;

        Group g = new Group(arr);
        pq.add(g);

    }

    public static boolean check(int y, int x) {
        if(y < 0 || x < 0 || x >= n || y >= n) return false;
        return true;
    }

    public static void rotateMap() {
        int[][] cloneMap = new int[n][n];
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                cloneMap[i][j] = map[i][j];
            }
        }

        // 1 2 3 4    4 4 4 4   0 3 -> 3 0
        // 1 2 3 4    3 3 3 3   1 3 -> 3 1
        // 1 2 3 4    2 2 2 2
        // 1 2 3 4    1 1 1 1   3 3 -> 0 3
        
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                map[n-1-j][i] = cloneMap[i][j];
            }
        }

    }

    public static void gravity() {
        // 각 열마다
        for(int c=0; c<n; c++) {
            // 각 행 밑바닥부터
            for(int r=n-1; r>=0; r--) {
                // 돌이면 아무수행안함
                if(map[r][c] == -1) continue;

                if(map[r][c] == -2) {
                    int upIndex = r-1;

                    while(upIndex >= 0 && map[upIndex][c] == -2) {
                        upIndex -= 1;
                    }

                    if(upIndex >= 0 && map[upIndex][c] != -1) {
                        map[r][c] = map[upIndex][c];
                        map[upIndex][c] = -2;
                    }
                }
            }
        }
    }

    public static void printMap() {
        for(int r=0; r<n; r++) {
            for(int c=0; c<n; c++) {
                System.out.print(map[r][c] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}