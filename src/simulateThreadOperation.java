import java.util.ArrayList;
import java.util.Scanner;

/*
.......................阿弥陀佛......................
.                       _oo0oo_                      .
.                      o8888888o                     .
.                      88" . "88                     .
.                      (| -_- |)                     .
.                      0\  =  \0                     .
.                   ___\‘---’\___                   .
.                  .' \|       |\ '.                 .
.                 \ \\|||  :  |||\\ \                .
.                \ _||||| -卍-|||||_ \               .
.               |   | \\\  -  \\\ |   |              .
.               | \_|  ''\---\''  |_\ |              .
.               \  .-\__  '-'  ___\-. \              .
.             ___'. .'  \--.--\  '. .'___            .
.         ."" ‘<  ‘.___\_<|>_\___.’>’ "".          .
.       | | :  ‘- \‘.;‘\ _ \’;.’\ - ’ : | |        .
.         \  \ ‘_.   \_ __\ \__ _\   .-’ \  \        .
.    =====‘-.____‘.___ \_____\___.-’___.-’=====     .
.                       ‘=---=’                      .
.                                                    .
.....................佛祖保佑 ,永无BUG..................
*/
public class simulateThreadOperation extends Thread{
    //进程队列
    static ArrayList<Process> Ready_lists = new ArrayList<Process>();//就绪队列
    static ArrayList<Process> Wait_lists = new ArrayList<Process>();//等待队列
    static ArrayList<Process> Running_lists = new ArrayList<Process>();//运行队列
    //多级反馈队列
    static ArrayList<Process> Ready_Lists1=new ArrayList<Process>();//多级队列1,时间片为5
    static ArrayList<Process> Ready_Lists2=new ArrayList<Process>();//多级队列2,时间片为20
    static ArrayList<Process> Ready_Lists3=new ArrayList<Process>();//多级队列3,时间片为用完
    //时间片设置
    static int timeSlice=5;//默认为5秒

    public static void Screen(timeCount t) throws InterruptedException {
        while(true) {
            //显示页面
            Scanner sc = new Scanner(System.in);
            System.out.println("------------------------------------------------------------");
            System.out.println("1.创建进程                          2.将等待进程变为就绪");
            System.out.println("3.查看当前就绪进程队列                4.查看当前等待队列");
            System.out.println("5.执行就绪队列(先到先服务原则)         6.更改进程优先级");
            System.out.println("7.执行就绪队列(高优先级原则)           8.执行就绪队列(时间片轮换)");
            System.out.println("9.更改进程需要的时间片                10.执行就绪队列(响应比优先)");
            System.out.println("11.执行就绪队列(短作业优先算法)        12.执行就绪队列(多级反馈队列算法)");
            System.out.println("0.登出程序");
            System.out.println("------------------------------------------------------------");
            System.out.print("请输入要进行的操作编号：");
            int choice = sc.nextInt();
            if (choice == 0){
                t.stop();
                break;
            }
            switch (choice) {
                case 1: {
                    creatProcess(t);
                    break;
                }
                case 2:{
                    WaitToReady(t);
                    break;
                }
                case 3:{
                    showReadyProcess();
                    break;
                }
                case 4:{
                    showWaitList();
                    break;
                }
                case 5:{
                    RunFCFS();
                    break;
                }
                case 6:{
                    ChangeProcessPriority();
                    break;
                }
                case 7:{
                    HPF();
                    break;
                }
                case 8:{
                    RunTimeSlice();
                    break;
                }
                case 9:{
                    changeTimeSlice();
                    break;
                }
                case 10:{
                    HRRF(t);
                    break;
                }
                case 11:{
                    SJF();
                    break;
                }
                case 12:{
                    MFQ();
                    break;
                }
                default:{
                    System.out.println("输入的编号有误，请重新输入");
                    break;
                }
            }
        }
    }
    public static void creatProcess(timeCount t){
        //创建进程
        Scanner sc=new Scanner(System.in);
        System.out.print("请输入需要创建的进程id:");
        int pid=sc.nextInt();
        if(!checkProcess(pid)){
            System.out.print("请输入进程所需要的时间：");
            int time=sc.nextInt();
            System.out.print("请输入进程名:");
            String name=sc.next();
            Process p=new Process(pid,name,time);
            p.setState(1);//设为就绪状态
            p.setArrivedTime(t.getTime());//设置到达时间
            Ready_lists.add(p);//加入就绪队列
            System.out.println("进程建立成功!");
            System.out.println();
        }else{
            System.out.println("该进程号:("+pid+")已经存在!");
        }

    }

    public static void showReadyProcess(){
        System.out.println("就绪队列为：");
        System.out.println("------------------------------");
        for(int i=0;i<Ready_lists.size();i++){
            System.out.println(Ready_lists.get(i).toString());
        }
        System.out.println("------------------------------");
    }

    public static boolean checkProcess(int pid){
        //判断此进程号是否存在
        int ifExist=0;//默认进程不存在
        for(int i=0;i<Ready_lists.size();i++){
            if(pid==Ready_lists.get(i).getProcess_id()){
                ifExist=1;
            }
        }
        for(int i=0;i<Wait_lists.size();i++){
            if(pid==Wait_lists.get(i).getProcess_id()){
                ifExist=1;
            }
        }
        for(int i=0;i<Running_lists.size();i++){
            if(pid==Running_lists.get(i).getProcess_id()){
                ifExist=1;
            }
        }
        if(ifExist==1){
            return true;//进程存在
        }else{
            return false;//进程不存在
        }
    }

    public static void WaitToReady(timeCount t){
        //将等待状态的进程变为就绪状态
        Scanner sc=new Scanner(System.in);
        System.out.print("请输入进程号：");
        int pid=sc.nextInt();
        int ifExist=0;//设置一个标记，表示找不到该进程号
        for(int i=0;i<Wait_lists.size();i++){
            if(pid==Wait_lists.get(i).getProcess_id()){
                Wait_lists.get(i).setState(1);//将该对象状态调整为就绪
                Wait_lists.get(i).setArrivedTime(t.getTime());//设置进入队列的时间
                Ready_lists.add(Wait_lists.get(i));//先将该对象入队就绪状态队列
                Wait_lists.remove(i);//再将该对象出队
                ifExist=1;
            }
        }
        if(ifExist==0){
            System.out.println("找不到该进程号！");
        }else {
            System.out.println("进程转换成功！");
        }

    }

    public static void showWaitList(){
        //显示当前等待进程队列
        System.out.println("等待队列为：");
        System.out.println("------------------------------");
        for(int i=0;i<Wait_lists.size();i++){
            System.out.println(Wait_lists.get(i).toString());
        }
        System.out.println("------------------------------");
    }

    public static void RunFCFS() throws InterruptedException {
        //先到先服务执行进程
        while (Ready_lists.size()!=0){
            System.out.println("-------------------------------------------");
            System.out.println("当前进程数为："+Ready_lists.size());
            Process p=Ready_lists.get(0);//得到队列的第一个进程
            int time=p.getProcess_time();//得到该进程需要运行的时间
            int showTime=time;
            for(int i=0;i<time;i++){
                System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余："+showTime+"秒");
                Thread.sleep(1000);
                showTime--;
            }
            System.out.println("进程："+p.getProcess_id()+"执行完成!");
            Ready_lists.remove(0);
        }
        System.out.println("就绪队列均已执行完！");
    }

    public static void ChangeProcessPriority(){
        System.out.print("请输入需要更改优先级的进程号:");
        Scanner sc=new Scanner(System.in);
        int pid=sc.nextInt();
        //更改进程优先级
        if(!checkProcess(pid)){
            System.out.println("该进程不存在！");
        }else{
            //判断此进程号是否存在
            int ifExist=0;//默认进程不存在
            for(int i=0;i<Ready_lists.size()&&ifExist==0;i++){
                if(pid==Ready_lists.get(i).getProcess_id()){
                    ifExist=1;
                    System.out.println("进程("+pid+")优先级为："+Ready_lists.get(0).getProcess_priority());
                    System.out.print("请输入更改后的优先级：");
                    int newPriority=sc.nextInt();
                    Ready_lists.get(i).setProcess_priority(newPriority);
                    System.out.println("更改进程优先级成功");
                }
            }
            for(int i=0;i<Wait_lists.size()&&ifExist==0;i++){
                if(pid==Wait_lists.get(i).getProcess_id()){
                    ifExist=1;
                    System.out.println("进程("+pid+")优先级为："+Wait_lists.get(0).getProcess_priority());
                    System.out.print("请输入更改后的优先级：");
                    int newPriority=sc.nextInt();
                    Wait_lists.get(i).setProcess_priority(newPriority);
                    System.out.println("更改进程优先级成功");
                }
            }
            for(int i=0;i<Running_lists.size()&&ifExist==0;i++){
                if(pid==Running_lists.get(i).getProcess_id()){
                    ifExist=1;
                    System.out.println("进程("+pid+")优先级为："+Running_lists.get(0).getProcess_priority());
                    System.out.print("请输入更改后的优先级：");
                    int newPriority=sc.nextInt();
                    Running_lists.get(i).setProcess_priority(newPriority);
                    System.out.println("更改进程优先级成功");
                }
            }
        }

    }

    public static void HPF() throws InterruptedException {
        //优先权高者优先
        while(Ready_lists.size()!=0){
            System.out.println("-------------------------------------------");
            System.out.println("当前进程数为："+Ready_lists.size());
            //先寻找队列中优先级最高的进程并出队
            int highestPriority=Ready_lists.get(0).getProcess_priority();//默认第一个为最高优先级
            int PriorityNum=0;//表示最高优先级的是第一个
            for(int i=1;i<Ready_lists.size();i++){
                if(Ready_lists.get(i).getProcess_priority()>highestPriority){
                    highestPriority=Ready_lists.get(i).getProcess_priority();
                    PriorityNum=i;
                }
            }
            Process p=Ready_lists.get(PriorityNum);//得到队列运行的进程
            int time=p.getProcess_time();//得到该进程需要运行的时间
            int showTime=time;
            for(int i=0;i<time;i++){
                System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余："+showTime+"秒");
                Thread.sleep(1000);
                showTime--;
            }
            System.out.println("进程："+p.getProcess_id()+"执行完成!");
            Ready_lists.remove(PriorityNum);
        }
        System.out.println("进程均已运行结束!");
    }

    public static void changeTimeSlice(){
        System.out.println("当前的时间片为："+timeSlice);
        Scanner sc=new Scanner(System.in);
        System.out.print("请输入更改后的时间片：");
        int changedTimeSlice=sc.nextInt();
        timeSlice=changedTimeSlice;
        System.out.println("时间片更改成功！");
    }

    public static void RunTimeSlice() throws InterruptedException {
        //时间片轮换执行进程
        while (Ready_lists.size()!=0){
            System.out.println("-------------------------------------------");
            System.out.println("当前进程数为："+Ready_lists.size());
            Process p=Ready_lists.get(0);//得到队列的第一个进程
            int time=p.getProcess_time();//得到该进程需要运行的时间
            int showTime=time;
            if(time<timeSlice){
                for(int i=0;i<time;i++){
                    System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余："+showTime+"秒");
                    Thread.sleep(1000);
                    showTime--;
                }
                p.setProcess_time(0);
            }else{
                for(int i=0;i<timeSlice;i++){
                    System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余："+showTime+"秒");
                    Thread.sleep(1000);
                    showTime--;
                }
                p.setProcess_time(time-timeSlice);
            }
            System.out.println("进程："+p.getProcess_id()+"执行完成!");
            if(p.getProcess_time()==0){
                //进程时间用完就离开队列
                Ready_lists.remove(0);
            }else{
                //没用完的话进入队列尾
                Ready_lists.remove(0);
                Ready_lists.add(p);
            }
        }
        System.out.println("就绪队列均已执行完！");
    }

    public static void HRRF(timeCount t) throws InterruptedException {
        //响应比优先(等待时间/所需时间)

        while(Ready_lists.size()>0){
            //1.获取当前时刻
            int timeNow=t.getTime();
            for(int i=0;i<Ready_lists.size();i++){
                //获取队列中的对象，并为他们都设置好响应比
                Process p=Ready_lists.get(i);
                p.setResponseRate(1.0*(timeNow-p.getArrivedTime())/(1.0*p.getProcess_time()));//这里输进去的都是0
            }
            System.out.println("-------------------------------------------");
            System.out.println("当前进程数为："+Ready_lists.size());
            //2.寻找队列中响应比最高的进程并出队
            double highestPriority=Ready_lists.get(0).getResponseRate();//默认第一个为最高响应比
            int PriorityNum=0;//表示最高响应比的是第一个
            for(int i=1;i<Ready_lists.size();i++){
                if(Ready_lists.get(i).getResponseRate()>highestPriority){
                    highestPriority=Ready_lists.get(i).getResponseRate();
                    PriorityNum=i;
                }
            }
            Process p=Ready_lists.get(PriorityNum);//得到队列运行的进程
            int time=p.getProcess_time();//得到该进程需要运行的时间
            int showTime=time;
            for(int i=0;i<time;i++){
                System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余："+showTime+"秒");
                Thread.sleep(1000);
                showTime--;
            }
            System.out.println("进程："+p.getProcess_id()+"执行完成!");
            Ready_lists.remove(PriorityNum);

        }
        System.out.println("就绪队列均已执行完!");
    }

    public static void SJF() throws InterruptedException {
        //短作业优先算法
        while(Ready_lists.size()!=0){
            System.out.println("-------------------------------------------");
            System.out.println("当前进程数为："+Ready_lists.size());
            //先寻找队列中所需时间最低的进程并出队
            int highestPriority=Ready_lists.get(0).getProcess_time();//默认第一个为作业时间最低
            int PriorityNum=0;//表示作业时间最低的是第一个
            for(int i=1;i<Ready_lists.size();i++){
                if(Ready_lists.get(i).getProcess_time()<highestPriority){
                    highestPriority=Ready_lists.get(i).getProcess_time();
                    PriorityNum=i;
                }
            }
            Process p=Ready_lists.get(PriorityNum);//得到队列运行的进程
            int time=p.getProcess_time();//得到该进程需要运行的时间
            int showTime=time;
            for(int i=0;i<time;i++){
                System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余："+showTime+"秒");
                Thread.sleep(1000);
                showTime--;
            }
            System.out.println("进程："+p.getProcess_id()+"执行完成!");
            Ready_lists.remove(PriorityNum);
        }
        System.out.println("进程均已运行结束!");
    }

    public static void MFQ() throws InterruptedException {
        //多级调节调度算法
        while (Ready_lists.size()!=0||Ready_Lists1.size()!=0||Ready_Lists2.size()!=0||Ready_Lists3.size()!=0){
            System.out.println("-------------------------------------------");
            int size=Ready_lists.size()+Ready_Lists1.size()+Ready_Lists2.size()+Ready_Lists3.size();
            System.out.println("当前进程数为："+size);
            if(Ready_lists.size()!=0){
                //该队列的时间片为1
                Process p=Ready_lists.get(0);
                int time=p.getProcess_time();
                if(p.getProcess_time()==1){
                    System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余：1秒");
                    Thread.sleep(1000);
                    Ready_lists.remove(0);
                }else{
                    System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余："+time+"秒");
                    Thread.sleep(1000);
                    p.setProcess_time(time-1);
                    Ready_lists.remove(0);
                    Ready_Lists1.add(p);
                }
            }else if(Ready_Lists1.size()!=0){
                //该队列的时间片为5
                Process p=Ready_Lists1.get(0);
                int time=p.getProcess_time();
                if(p.getProcess_time()<5){
                    for(int i=0;i<p.getProcess_time();i++){
                        System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余："+time+"秒");
                        time--;
                        Thread.sleep(1000);
                    }
                    Ready_Lists1.remove(0);
                }else{
                    for(int i=0;i<5;i++){
                        System.out.println("进程："+p.getProcess_id()+"正在执行中......剩余："+time+"秒");
                        time--;
                        Thread.sleep(1000);
                    }
                    p.setProcess_time(p.getProcess_time()-5);
                    Ready_Lists1.remove(0);
                    Ready_Lists2.add(p);
                }
            }else if(Ready_Lists2.size()!=0) {
                //该队列的时间片为20
                Process p = Ready_Lists2.get(0);
                int time = p.getProcess_time();
                if (p.getProcess_time() < 20) {
                    for (int i = 0; i < p.getProcess_time(); i++) {
                        System.out.println("进程：" + p.getProcess_id() + "正在执行中......剩余：" + time + "秒");
                        time--;
                        Thread.sleep(1000);
                    }
                    Ready_Lists2.remove(0);
                } else {
                    for (int i = 0; i < 20; i++) {
                        System.out.println("进程：" + p.getProcess_id() + "正在执行中......剩余：" + time + "秒");
                        time--;
                        Thread.sleep(1000);
                    }
                    p.setProcess_time(p.getProcess_time() - 20);
                    Ready_Lists2.remove(0);
                    Ready_Lists3.add(p);
                }
            }else if(Ready_Lists3.size()!=0){
                //该队列的时间片为用完
                Process p = Ready_Lists3.get(0);
                int time = p.getProcess_time();
                for (int i = 0; i < p.getProcess_time(); i++) {
                    System.out.println("进程：" + p.getProcess_id() + "正在执行中......剩余：" + time + "秒");
                    time--;
                    Thread.sleep(1000);
                }
                Ready_Lists3.remove(0);
            }

        }
        System.out.println("就绪队列均已执行完！");
    }



    public static void main(String[] args) throws InterruptedException {
        //开启计时器线程
        timeCount t=new timeCount();
        t.start();
        //主页面
        Screen(t);
    }
}

