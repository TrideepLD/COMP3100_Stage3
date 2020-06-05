public class Server {

	public int id;
	public String type;
	public int limit;
	public int bootupTime;
	public float rate;
	public int coreCount;
	public int memory;
	public int disk;
	public int state;
	public int availableTime;

	Server(int id, String t, int l, int b, float r, int c, int m, int d) {
		this.id = id;
		this.type = t;
		this.limit = l;
		this.bootupTime = b;
		this.rate = r;
		this.coreCount = c;
		this.memory = m;
		this.disk = d;
	}

	Server(String type, int id, int state, int availableTime, int coreCount, int memory, int disk) {
		this.type = type;
		this.id = id;
		this.state = state;
		this.availableTime = availableTime;
		this.coreCount = coreCount;
		this.memory = memory;
		this.disk = disk;
	}
}