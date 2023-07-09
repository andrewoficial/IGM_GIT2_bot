package resources;

public enum Repositories {
    //LocalPortal("LocalPortal", "-823582989"),
    LocalPortal("LocalPortal", "1811468195"),
    IGM_13M("IGM_13M", "-823582989"),
    LongGas("LongGas", "-823582989"),
    PolarisMath("PolarisMath", "-823582989"),
    ard_ccm_fee("ard_ccm_fee", "-823582989"),
    Multigassens("Multigassens", "-823582989"),
    RelayArduinoSalavat("RelayArduinoSalavat", "-823582989"),
    DVK("4CHANNELS", "-823582989"),
    Basement("IGM-Basement", "-823582989"),
    IPP330("IPP330", "-823582989"),
    IGM10M_Tool("IGM10M_Tool", "-823582989"),
    RAK811_Multigassense_Remote_Control("RAK811_Multigassense_Remote_Control", "-823582989"),
    RAK811_LVS("RAK811_LVS", "-823582989"),
    PagTool("PagTool", "-823582989"),
    IGM12M_IGM13M("IGM12M_IGM13M", "-823582989"),
    VegaConnect("VegaConnect", "-823582989"),
    VOC("VOC", "-823582989"),
    DEFAULT("NotDefined", "1811468195");


    private String repoName;
    private String tgId;
    private Repositories(String repoName, String tgId){
        this.repoName=repoName;
        this.tgId = tgId;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getTgId() {
        return tgId;
    }


}
