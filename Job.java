public class Job {

	public int submitTime;
	public int id;
	public int estRuntime;
	public int cpuCores;
	public int memory;
	public int disk;

	Job(int st, int id, int r, int c, int m, int d) {
		this.submitTime = st;
		this.id = id;
		this.estRuntime = r;
		this.cpuCores = c;
		this.memory = m;
		this.disk = d;
	}
}