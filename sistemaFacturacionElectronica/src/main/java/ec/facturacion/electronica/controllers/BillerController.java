package ec.facturacion.electronica.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;

import ec.facturacion.electronica.controllers.util.JsfUtil;
import ec.facturacion.electronica.entities.Bill;
import ec.facturacion.electronica.entities.BillDetail;
import ec.facturacion.electronica.entities.Client;
import ec.facturacion.electronica.entities.Product;
import ec.facturacion.electronica.entities.User;
import ec.facturacion.electronica.services.BillFacade;
import ec.facturacion.electronica.services.ClientFacade;
import ec.facturacion.electronica.services.ProductFacade;

@ManagedBean(name = "billerController")
@SessionScoped
public class BillerController implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5226882634455572968L;
	@EJB
	private BillFacade ejbBillFacade;
	@EJB
	private ClientFacade ejbClientFacade;
	@EJB
	private ProductFacade ejbProductFacade;
	private Bill bill = new Bill();
	private Client client = new Client();
	private String idClient = new String();
	private String nombreProducto = "";
	private String codigoProducto = "";
	private List<Product> lstProducts = new ArrayList<Product>();
	private User active;
	private List<Product> lstSelectedProducts = new ArrayList<Product>();
	private List<BillDetail> lstBillDetail = new ArrayList<BillDetail>();

	public BillerController() {
	}
	
	public void init(User user){
		active = user;
		bill = new Bill();
		client = new Client();
		idClient = "";
		bill.setBilDate(new Date());
		nombreProducto = "";
		codigoProducto = "";
		lstProducts = new ArrayList<Product>();
		lstSelectedProducts = new ArrayList<Product>();
		lstBillDetail = new ArrayList<BillDetail>();
	}
	
	public void update(){
		try{
			if(client != null && client.getCliCode() != null){
				ejbClientFacade.merge(client);
			}else{
				ejbClientFacade.persist(client);
			}
			bill.setCliCode(client);
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('ClientEditDialog').hide();");
		}catch (Exception ex){
			JsfUtil.addErrorMessage(ex,"No pudo ser asignado el cliente");
		}
		
	}
	
	public int showBillNumber(){
		return ejbBillFacade.count()+1;
	}
	
	public void editClient(){
		if(bill.getCliCode() != null){
			client = bill.getCliCode();
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('ClientEditDialog').show();");
		}else{
			JsfUtil.addErrorMessage("No existe un cliente asignado a la Factura");
		}
	}
	
	public void findClient(){
		client = new Client();
		client = ejbClientFacade.findById(idClient);
		if(client == null){
			client = new Client();
			client.setCliEnabled(new Boolean(true));
			client.setCliId(idClient);
		}
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('ClientEditDialog').show();");
	}
	
	public void findProduct(){
		if(!nombreProducto.isEmpty()){
			nombreProducto = "%" + nombreProducto + "%";
		}
		if(!codigoProducto.isEmpty()){
			codigoProducto = "%" + codigoProducto + "%";
		}
		lstProducts = ejbProductFacade.findByNameAndCodeAux(true, nombreProducto, codigoProducto, active);
		lstProducts.size();
	}
	
	public void openProductSearch(){
		nombreProducto = "";
		codigoProducto = "";
		lstProducts = new ArrayList<Product>();
		lstSelectedProducts = new ArrayList<Product>();
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('productDialog').show();");
	}
	
	public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        
        lstBillDetail.get(event.getRowIndex()).setTotal(lstBillDetail.get(event.getRowIndex()).getProCode().getProValue() * (Float) newValue);
         
        if(newValue != null && !newValue.equals(oldValue)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
	
	public void addProduct(){
		try{
			if(lstSelectedProducts != null && !lstSelectedProducts.isEmpty()){
				for(Product prod:lstSelectedProducts){
					BillDetail billDetail = new BillDetail();
					billDetail.setBilCode(bill);
					billDetail.setProCode(prod);
					lstBillDetail.add(billDetail);
				}
				RequestContext context = RequestContext.getCurrentInstance();
				context.execute("PF('productDialog').hide();");
			}
		}catch(Exception ex){
			JsfUtil.addErrorMessage(ex,"No se pudo asignar los productos seleccionados");
		}
		
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
	}
	
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getIdClient() {
		return idClient;
	}

	public void setIdClient(String idClient) {
		this.idClient = idClient;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public String getCodigoProducto() {
		return codigoProducto;
	}

	public void setCodigoProducto(String codigoProducto) {
		this.codigoProducto = codigoProducto;
	}

	public List<Product> getLstProducts() {
		return lstProducts;
	}

	public void setLstProducts(List<Product> lstProducts) {
		this.lstProducts = lstProducts;
	}

	public User getActive() {
		return active;
	}

	public void setActive(User active) {
		this.active = active;
	}

	public List<Product> getLstSelectedProducts() {
		return lstSelectedProducts;
	}

	public void setLstSelectedProducts(List<Product> lstSelectedProducts) {
		this.lstSelectedProducts = lstSelectedProducts;
	}

	public List<BillDetail> getLstBillDetail() {
		return lstBillDetail;
	}

	public void setLstBillDetail(List<BillDetail> lstBillDetail) {
		this.lstBillDetail = lstBillDetail;
	}
	
	
}