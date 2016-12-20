package tpwinr;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Comparator;

public class TagProAnalyticsWinRateByMap {
	
	public static void main(String[] args) throws IOException {
		String s = "";
		Scanner sc = new Scanner(System.in);
		System.out.println("What is your TagPro name?");
		String name = sc.nextLine().replace(" ", "+");
		System.out.println("How many pages of your tagpro.eu games would you like to check? (1 page = 50 matches, including group matches)");
		int j = sc.nextInt(); j++;
		while(!s.toLowerCase().equals("y") && !s.toLowerCase().equals("n")){
			System.out.println("Include non-public matches? (y/n)");
			s = sc.nextLine();
		}
		boolean op = s.toLowerCase().equals("y");
		while(!s.toLowerCase().equals("y") && !s.toLowerCase().equals("n")){
			System.out.println("Include matches where you had stats off? (y/n)");
			s = sc.nextLine();
		}
		boolean so = s.toLowerCase().equals("y");
		sc.close();
		System.out.println("Reading tagpro.eu data...");
		s = "";
		for(int i = 1; i < j; i ++){
			s += getUrlSource("https://tagpro.eu/?search=player&name=" + name + "&page=" + i);
		}
		System.out.println("Done reading data.");
		String strs[] = s.split("\\?map\\=");
		ArrayList<String> maps = new ArrayList<String>();
		ArrayList<Integer> wins = new ArrayList<Integer>();
		ArrayList<Integer> games = new ArrayList<Integer>();
		int n = 1;
		String map = " ";
		for(String st : strs){
			if(op || st.contains("public") && so || st.contains("°")){
				if(!map.equals(" ")){
					map = st.split(">")[1].split("<")[0];
					map = map.substring(0, Math.min(map.length(), 15));
					while(map.length() < 12) map = map + "    ";
					int l  = maps.lastIndexOf(map);
					if(l < 0){
						maps.add(map);
						wins.add(0);
						games.add(1);
						l = maps.size() - 1;
					}
					else{
						games.set(l, games.get(l) + 1);
					}
					if(st.contains("✓<")){
						wins.set(l, wins.get(l) + 1);
						if(wins.get(l) > n) n ++;
					}
				}
				map = st.split(">")[st.split(">").length - 1];
			}
		}
		String lines[] = new String[maps.size()];
		for(int i = 0; i < maps.size(); i ++){
			lines[i] = maps.get(i) + "\t" + wins.get(i) + "\t" + games.get(i) + "\t" + (100 * wins.get(i)) / games.get(i) + "%";
		}
		System.out.println("Sorting maps...");
		Arrays.sort(lines, new TagProAnalyticsWinRateByMap().new LineComparator());
		System.out.println("\nMap  \tWins\tGames\tWin %");
		for(String line: lines) if(!line.substring(0, 7).equals("Death T")) System.out.println(line);
	}
	
	private static String getUrlSource(String url) throws IOException {
		URLConnection page = new URL(url).openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(page.getInputStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String inrl = in.readLine();
		while (inrl != null){
			sb.append(inrl);
			inrl = in.readLine();
		}
		in.close();
		return sb.toString();
	}
	
	public class LineComparator implements Comparator<String>{
		public int compare(String arg0, String arg1) {
			int a = Integer.valueOf(arg0.split("\t")[2]) - Integer.valueOf(arg1.split("\t")[2]);
			if(a > 0) return -1;
			if(a < 0) return 1;
			return 0;
		}
	}
	
}
