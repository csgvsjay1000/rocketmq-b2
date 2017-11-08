package invengo.cn.rocketmq.common.namesrv;

import java.io.File;

public class NamesrvConfig {
	private String rocketmqHome = "rocketmqHome";

    private String kvConfigPath = System.getProperty("user.home") + File.separator + "namesrv" + File.separator + "kvConfig.json";
    private String configStorePath = System.getProperty("user.home") + File.separator + "namesrv" + File.separator + "namesrv.properties";
    private String productEnvName = "center";
    private boolean clusterTest = false;
    private boolean orderMessageEnable = false;
    
	public String getRocketmqHome() {
		return rocketmqHome;
	}
	public void setRocketmqHome(String rocketmqHome) {
		this.rocketmqHome = rocketmqHome;
	}
	public String getKvConfigPath() {
		return kvConfigPath;
	}
	public void setKvConfigPath(String kvConfigPath) {
		this.kvConfigPath = kvConfigPath;
	}
	public String getConfigStorePath() {
		return configStorePath;
	}
	public void setConfigStorePath(String configStorePath) {
		this.configStorePath = configStorePath;
	}
	public String getProductEnvName() {
		return productEnvName;
	}
	public void setProductEnvName(String productEnvName) {
		this.productEnvName = productEnvName;
	}
	public boolean isClusterTest() {
		return clusterTest;
	}
	public void setClusterTest(boolean clusterTest) {
		this.clusterTest = clusterTest;
	}
	public boolean isOrderMessageEnable() {
		return orderMessageEnable;
	}
	public void setOrderMessageEnable(boolean orderMessageEnable) {
		this.orderMessageEnable = orderMessageEnable;
	}
    
    
}
