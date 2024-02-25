import java.util.*;
import java.io.*;

// start: 12: 13 
// end: 13: 41
// 1h 28m
public class Main {

    static int N;
    static int[][] map;

    static class Point {
        int y, x;
        Point(int y, int x) {
            this.y = y;
            this.x = x;
        }
    }

    static class Group {
        int num;
        int value;
        ArrayList<Point> points;

        Group(int num, int value, ArrayList<Point> points) {
            this.num = num;
            this.value = value;
            this.points = points;
        }
    }

    static int[] dy = {-1, 0, 1, 0};
    static int[] dx = {0, 1, 0, -1};
    static int[][] groupNumber; // 각좌표가 어떤 그룹에 속하는지
    static int[][] betweenLineCount; // r, c사이의 맞닿는 변의 개수
    static HashMap<Integer, Group> hashMap;
    static int answer = 0;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        N = Integer.parseInt(br.readLine()); // 맵 한변의 길이

        // 맵 입력 받기
        map = new int[N+1][N+1];
        for(int r=1; r<=N; r++) {
            StringTokenizer st = new StringTokenizer(br.readLine());
            for(int c=1; c<=N; c++) {
                map[r][c] = Integer.parseInt(st.nextToken());
            }
        }
        

        for(int i=0; i<4; i++) {
            makeGroups();
            // 예술점수 계산
            calculateScore();
            rotateCross();
            rotateSquare();
        }

        System.out.println(answer);
    }

    // 그룹 묶기
    public static void makeGroups() {
        // key: 그룹번호, value: Group객체
        hashMap = new HashMap<>();

        groupNumber = new int[N+1][N+1];
        int groupNum = 1;
        boolean[][] visited = new boolean[N+1][N+1];
        for(int i=1; i<=N; i++) {
            for(int j=1; j<=N; j++) {
                if(!visited[i][j]) {
                    ArrayList<Point> arr = new ArrayList<>();
                    
                    Queue<Point> queue = new ArrayDeque<>();
                    visited[i][j] = true;
                    groupNumber[i][j] = groupNum;
                    arr.add(new Point(i, j));
                    queue.add(new Point(i, j));

                    while(!queue.isEmpty()) {
                        Point p = queue.poll();

                        for(int k=0; k<4; k++) {
                            int nextY = p.y + dy[k];
                            int nextX = p.x + dx[k];

                            if(check(nextY, nextX) && !visited[nextY][nextX] && map[nextY][nextX] == map[i][j]) {
                                visited[nextY][nextX] = true;
                                arr.add(new Point(nextY, nextX));
                                queue.add(new Point(nextY,nextX));
                                groupNumber[nextY][nextX] = groupNum;
                            }
                        }
                    }

                    hashMap.put(groupNum, new Group(groupNum, map[i][j], arr));
                    groupNum += 1;
                }
            }
        }

        int groupsLen = hashMap.keySet().size();

        betweenLineCount = new int[groupsLen+1][groupsLen+1];
        for(int i=1; i<=N; i++) {
            for(int j=2; j<=N; j++) {
                if(groupNumber[i][j] != groupNumber[i][j-1]) {
                    betweenLineCount[groupNumber[i][j]][groupNumber[i][j-1]] += 1;
                    betweenLineCount[groupNumber[i][j-1]][groupNumber[i][j]] += 1;
                }

                if(groupNumber[j][i] != groupNumber[j-1][i]) {
                    betweenLineCount[groupNumber[j][i]][groupNumber[j-1][i]] += 1;
                    betweenLineCount[groupNumber[j-1][i]][groupNumber[j][i]] += 1;
                }
            }
        }     
    }

    // 회색 십자가 돌리기
    public static void rotateCross() {
        Stack<Integer> horizontal = new Stack<>();
        Queue<Integer> vertical = new ArrayDeque<>();

        int mid = N / 2 + 1;

        for(int i=1; i<=N; i++) {
            vertical.add(map[i][mid]);
            horizontal.add(map[mid][i]);
        }

        for(int i=1; i<=N; i++) {
            map[i][mid] = horizontal.pop();
            map[mid][i] = vertical.poll();
        }

        // for(int i=1; i<=N; i++) {
        //     for(int j=1; j<=N; j++) {
        //         System.out.print(map[i][j] + " ");
        //     }
        //     System.out.println();
        // }
        // System.out.println();
    }

    // 노란색 네모 4개 돌리기
    public static void rotateSquare() {
        rotateEachSquare(1, 1, N/2);
        rotateEachSquare(N/2+2, 1, N/2);
        rotateEachSquare(1, N/2+2, N/2);
        rotateEachSquare(N/2+2, N/2+2, N/2);
    }

    public static void rotateEachSquare(int startY, int startX, int len) {
        int[][] copy = new int[len][len];

        // (1, 1) -> (1, 5)
        // (2, 2) -> (2, 4)

        // 1 4 1, 0 -> 0 2
        // 2 4 -> 1, 4

        // start 4, 1 -> (0, 0) -> (0, )
        // 4, 1 -> 4, 2
        for(int row=startY; row<startY+len; row++) {
            for(int col=startX; col<startX+len; col++) {
                int r = row - startY;
                int c = col - startX;
                int nr = c;
                int nc = len-1-r;
                copy[nr][nc] = map[row][col];
            }
        }

        for(int row=0; row<len; row++) {
            for(int col=0; col<len; col++) {
                map[startY+row][startX+col] = copy[row][col];
            }
        }
    }

    // 예술점수 계산
    public static void calculateScore() {
        int score = 0;
        // 예술점수 계산
        for(int s1=1; s1<=hashMap.keySet().size(); s1++) {
            for(int s2=s1+1; s2<=hashMap.keySet().size(); s2++) {
                if(betweenLineCount[s1][s2] > 0) {
                    Group g1 = hashMap.get(s1);
                    Group g2 = hashMap.get(s2);

                    score += (betweenLineCount[s1][s2] * g2.value * g1.value * (g1.points.size() + g2.points.size()));
                    
                }
            }
        }
        
        answer += score;
    }
    
    // 좌표 유효성 검사
    public static boolean check(int y, int x) {
        if(y < 1 || x < 1 || x > N || y > N) {
            return false;
        }
        return true;
    }
}