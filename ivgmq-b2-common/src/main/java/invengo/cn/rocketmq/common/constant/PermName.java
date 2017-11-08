package invengo.cn.rocketmq.common.constant;

/**
 * 权限控制
 * */
public class PermName {
	public static final int PERM_INHERIT = 0x01 << 0;  //
	public static final int PERM_WRITE = 0x01 << 1;  //
	public static final int PERM_READ = 0x01 << 2;  //
	public static final int PERM_PRIORITY = 0x01 << 3;  //
	
	public static String perm2String(final int perm) {
        final StringBuffer sb = new StringBuffer("---");
        if (isReadable(perm)) {
            sb.replace(0, 1, "R");
        }

        if (isWriteable(perm)) {
            sb.replace(1, 2, "W");
        }

        if (isInherited(perm)) {
            sb.replace(2, 3, "X");
        }

        return sb.toString();
    }
	
	public static boolean isReadable(final int perm) {
        return (perm & PERM_READ) == PERM_READ;
    }

    public static boolean isWriteable(final int perm) {
        return (perm & PERM_WRITE) == PERM_WRITE;
    }

    public static boolean isInherited(final int perm) {
        return (perm & PERM_INHERIT) == PERM_INHERIT;
    }
	
}
