import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {
    static final int BLAST = -1;
    static final int BLAST_SIZE = 2;
    static int n,m,t;
    static int[][] map;
    static int[] blast;
    static int[] dx = {-1,1,0,0};
    static int[] dy = {0,0,-1,1};

    static class Location{
        int x;
        int y;

        public Location(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

        StringTokenizer st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        t = Integer.parseInt(st.nextToken());

        map = new int[n][m];
        blast = new int[BLAST_SIZE];
        int blastIdx = 0;
        for(int i=0;i<n;i++){
            st = new StringTokenizer(br.readLine());
            for(int j=0;j<m;j++){
                map[i][j] = Integer.parseInt(st.nextToken());

                if(map[i][j] == BLAST){
                    blast[blastIdx++] = i;
                }
            }
        }

        while(t-->0){
            map = changeMap();
            rotateUp();
            rotateDown();
            for(int x:blast){
                map[x][0] = BLAST;
            }
        }

        int sum = 0;
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                if(map[i][j] == BLAST){
                    continue;
                }
                sum += map[i][j];
            }
        }

        bw.write(Integer.toString(sum));
        bw.flush();

        br.close();
        bw.close();
    }

    public static int[][] changeMap(){
        int[][] changedMap = new int[n][m];
        for(int i=0;i<n;i++){
            for(int j=0;j<m;j++){
                if(map[i][j] == BLAST){
                    continue;
                }
                spreadDust(i,j,changedMap);
            }
        }

        return changedMap;
    }

    public static void spreadDust(int x, int y, int[][] changedMap){
        ArrayList<Location> changedLocations = new ArrayList<>();

        int origin = map[x][y];
        for(int i=0;i<4;i++){
            int nx = x + dx[i];
            int ny = y + dy[i];

            if(isOutOfMap(nx,ny)){
                continue;
            }

            if(map[nx][ny] == BLAST){
                continue;
            }

            changedLocations.add(new Location(nx,ny));
        }

        int splited = origin/5;
        changedMap[x][y] += origin - splited*changedLocations.size();
        for(Location changedLocation:changedLocations){
            changedMap[changedLocation.x][changedLocation.y] += splited;
        }
    }

    public static boolean isOutOfMap(int nx, int ny){
        return nx<0 || nx>=n || ny<0 || ny>=m;
    }

    public static void rotateUp(){
        int x = blast[0];

        for(int i=x;i>0;i--){ // 아래
            map[i][0] = map[i-1][0];
        }

        for(int i=1;i<m;i++){ // 왼쪽방향
            map[0][i-1] = map[0][i];
        }

        for(int i=1;i<=x;i++){
            map[i-1][m-1] = map[i][m-1];
        }

        for(int i=m-1;i>0;i--){ // 오른쪽
            map[x][i] = map[x][i-1];
        }

        map[x][1] = 0;
        map[x][0] = -1;
    }

    public static void rotateDown(){
        int x = blast[1];

        for(int i=x;i<n-1;i++){
            map[i][0] = map[i+1][0];
        }

        for(int i=1;i<m;i++){ // 왼쪽방향
            map[n-1][i-1] = map[n-1][i];
        }

        for(int i=n-1;i>x;i--){
            map[i][m-1] = map[i-1][m-1];
        }

        for(int i=m-1;i>0;i--){
            map[x][i] = map[x][i-1];
        }

        map[x][1] = 0;
        map[x][0] = -1;
    }
}