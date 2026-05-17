package com.jfinal.plugin.activerecord;

public class SqlLog {
    /**
     * 子线程可继承的线程局部变量
     */
    private static final InheritableThreadLocal<Boolean> logFlag = new InheritableThreadLocal<Boolean>();

    /**
     * 是否禁用日志
     * @return
     */
    public static Boolean isDisableLog(){
        return logFlag.get() == null?false:logFlag.get();
    }

    /**
     * 禁用sql日志
     * @param sqlRunner: 执行器
     * @time: 2022/8/12 13:34
     */
    public static <T> T disableSqlLog(ISqlRunner<T> sqlRunner){
        //是否存在父类禁用
        boolean hasParentDisable = logFlag.get()==null ? false : logFlag.get();
        logFlag.set(true);
        try{
            return sqlRunner.run();
        }catch (Throwable e){
            e.printStackTrace();
            if(e instanceof RuntimeException){
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e.getMessage());
        }finally {
            if(!hasParentDisable){
                //防止嵌套禁用，导致父类日志存在输出。
                logFlag.remove();
            }
        }
    }


    public static interface ISqlRunner<T>{
        T run();
    }
}
