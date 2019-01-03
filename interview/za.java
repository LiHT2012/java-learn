Class<MyThread> a = MyThread.class;
Class<?> b = Class.forName("com.dbcool.api.liht.MyThread");
MyThread m = new MyThread();
Class<? extends MyThread> c = m.getClass();
