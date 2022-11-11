public class Process {
    private int process_id;//进程编号,作为唯一标识
    private int process_priority=0;//进程优先级（默认都为0）
    private String name;//进程名
    private int state;//状态(0为等待，1为就绪，2为运行，3为死亡)
    private int process_time;//进程运行所需时间
    private int arrivedTime;//进程到达时间(用于计算响应比)
    private double responseRate;//响应比
    public Process(int process_id,int process_time){
        this.process_id=process_id;
        this.process_time=process_time;
    }
    public Process(int process_id,String name){
        this.process_id=process_id;
        this.name=name;
    }
    public Process(int process_id,String name,int process_time){
        this.process_id=process_id;
        this.name=name;
        this.process_time=process_time;
    }
    public Process(){}

    public int getProcess_id() {
        return process_id;
    }

    public void setProcess_id(int process_id) {
        this.process_id = process_id;
    }

    public int getProcess_priority() {
        return process_priority;
    }

    public void setProcess_priority(int process_priority) {
        this.process_priority = process_priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getProcess_time() {
        return process_time;
    }

    public void setProcess_time(int process_time) {
        this.process_time = process_time;
    }

    public int getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(int arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public double getResponseRate() {
        return responseRate;
    }

    public void setResponseRate(double responseRate) {
        this.responseRate = responseRate;
    }

    @Override
    public String toString() {
        return "Process{" +
                "process_id=" + process_id +
                ", process_priority=" + process_priority +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", process_time=" + process_time +
                ", arrivedTime=" + arrivedTime +
                '}';
    }
}
