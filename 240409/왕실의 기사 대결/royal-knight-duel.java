import java.io.*;
import java.util.*;

public class Main {
	
	static int L, N, Q;
	static int[][] map;
	
	static class Person {
		int index;
		int y, x;
		int width, height;
		int originalLife;
		int currentLife;
		
		Person(int index, int y, int x, int width, int height, int originalLife, int currentLife) {
			this.index = index;
			this.y = y;
			this.x = x;
			this.width = width;
			this.height = height;
			this.originalLife = originalLife;
			this.currentLife = currentLife;
		}
	}
	
	static Person[] personList;
	static int[][] position;
	
	static int[] dy = {1, 0, -1, 0};
	static int[] dx = {0, 1, 0, -1};

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		StringTokenizer st = new StringTokenizer(br.readLine());

		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());

		map = new int[L + 1][L + 1];
		for (int i = 1; i <= L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		personList = new Person[N+1];
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());

			personList[i] = new Person(i, r, c, w, h, k, k);
		}

		for (int __ = 1; __ <= Q; __++) {
			st = new StringTokenizer(br.readLine());
			int i = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			
			drawMap();
			
			// 명령에 따라 움직이게 한다.
			Queue<Integer> queue = new LinkedList<>();
			Stack<Integer> stack = new Stack<>();
			queue.add(i);
			stack.add(i);
			boolean[] check = new boolean[N+1];
			check[i] = true;
			
			boolean flag = true;
			
			while(!queue.isEmpty()) {
				int num = queue.poll();
				
				if(personList[num] == null) continue;
				Person p = personList[num];
				
				int nextY = p.y + dy[d];
				int nextX = p.x + dx[d];
				
				for(int y=nextY; y<nextY+p.height; y++) {
					for(int x=nextX; x<nextX+p.width; x++) {
						if(!check(y, x) || map[y][x] == 2) {
							flag = false;
							break;
						}
						
						//
						if(position[y][x] != p.index && position[y][x] >= 1) {
							int index = position[y][x];
							if(check[index]) continue;
							queue.add(index);
							stack.add(index);
							check[index] = true;
						}
						// 
					}
					if(!flag) break;
				}
				
				if(!flag) {
					queue.clear();
					stack.clear();
					break;
				}
			}
			 
			while(!stack.isEmpty()) {
				 int num = stack.pop();
				 
				 if(personList[num] == null) continue;
				 Person p = personList[num];
				 p.y += dy[d];
				 p.x += dx[d];
				 
				if(num == i) continue;
				
				for (int y = p.y; y < p.y + p.height; y++) {
					for (int x = p.x; x < p.x + p.width; x++) {
						if(map[y][x] == 1) {
							p.currentLife -= 1;
						}
					}
					if(p.currentLife <= 0) {
						personList[p.index] = null;
						break;
					}
				}
			 }
		}
		
		int answer = 0;
		for(int i=1; i<=N; i++) {
			if(personList[i] == null) continue;
			//System.out.println(i + " " + personList[i].currentLife);
			answer += (personList[i].originalLife - personList[i].currentLife);
		}
		System.out.println(answer);
		
	}
	
	public static void drawMap() {
		position = new int[L+1][L+1];
		
		for(int i=1; i<=N; i++) {
			if(personList[i] == null) continue;
			
			Person p = personList[i];
			
			for(int y=p.y; y<p.y+p.height; y++) {
				for(int x=p.x; x<p.x+p.width; x++) {
					position[y][x] = p.index;
				}
			}
		}
		
//		for(int i=1; i<=L; i++) {
//			for(int j=1; j<=L; j++) {
//				System.out.print(position[i][j] + " ");
//			}
//			System.out.println();
//		}
//		System.out.println();
	}
	
	public static boolean check(int y, int x) {
		if(y < 1 || x < 1 || x > L || y > L) {
			return false;
		}
		return true;                     
	}

}