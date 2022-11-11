public class timeCount extends Thread{
    static int time=0;
    public void run(){
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time=time+1;
        }
    }

    public int getTime() {
        return time;//获取当前时间
    }
}
