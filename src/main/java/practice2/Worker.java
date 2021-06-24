package practice2;

public class Worker extends Thread {

    private final int workState;
    private final Data data;

    public Worker(int id, Data d) {
        this.workState = id;
        data = d;
        this.start();
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                synchronized (data) {
                    while (workState != data.getState()) {
                            data.wait();
                    }
                    if (workState == 1) {
                        data.Tic();
                    } else if (workState==2) {
                        data.Tak();
                    }else data.Toy();
                    data.notifyAll();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
