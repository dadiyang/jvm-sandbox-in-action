/**
 * 报时的钟
 */
public class Clock {

    // 日期格式化
    private final java.text.SimpleDateFormat clockDateFormat
            = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 状态检查
     */
    public void checkState() {
         java.util.Date d = nowDate();
         long a = d.getTime();
         try{Thread.sleep(500);}catch(Exception e){e.printStackTrace();}
      //  throw new IllegalStateException("STATE ERROR!");
    }

    public void doSomeTimeComsumingOperation(){
        try{Thread.sleep(2000);}catch(Exception e){e.printStackTrace();}
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public java.util.Date nowDate() {
        return new java.util.Date();
    }

    private void doSomeFastOperation(){
        String s = "";
        for(int i=0;i<10000;i++){
            s += i;
        }
        new String(s);
    }
    /**
     * 报告时间
     *
     * @return 报告时间
     */
    public String report(java.util.Date now) {
        checkState();
        try{
           Thread.sleep(400);
        }catch(Exception e){ 
           e.printStackTrace();
        }
        doSomeTimeComsumingOperation();
        doSomeFastOperation();
        String n = clockDateFormat.format(now);
        System.out.println("report: " + n);
        return n;
    }

    /**
     * 循环播报时间
     */
    final void loopReport() throws InterruptedException {
        while (true) {
            try {
                System.out.println(report(nowDate()));
            } catch (Throwable cause) {
                cause.printStackTrace();
            }
            Thread.sleep(2000);
        }
    }

    public static void main(String... args) throws InterruptedException {
        new Clock().loopReport();
    }

}
