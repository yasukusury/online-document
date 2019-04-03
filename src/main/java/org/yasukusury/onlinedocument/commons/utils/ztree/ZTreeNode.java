package org.yasukusury.onlinedocument.commons.utils.ztree;


public class ZTreeNode {

    /**
     * 节点id
     */
    private String id;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 是否上级节点
     */
    private String pid;

    /**
     * 是否选中
     */
    private boolean checked;
    /**
     * 节点编码
     */
    private String code;

    public ZTreeNode() {
    }

    public ZTreeNode(String id, String name, String pid, boolean checked){
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
