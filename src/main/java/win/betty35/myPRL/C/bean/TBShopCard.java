package win.betty35.myPRL.C.bean;

public class TBShopCard 
{
	private float description,attitude,delivery;
	private String shop;
	private Long shopId;
	private Boolean isTmall;
	
	public Long getShopId() {
		return shopId;
	}
	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}
	public float getDescription() {
		return description;
	}
	public void setDescription(float description) {
		this.description = description;
	}
	public float getAttitude() {
		return attitude;
	}
	public void setAttitude(float attitude) {
		this.attitude = attitude;
	}
	public float getDelivery() {
		return delivery;
	}
	public void setDelivery(float delivery) {
		this.delivery = delivery;
	}
	public String getShop() {
		return shop;
	}
	public void setShop(String shop) {
		this.shop = shop;
	}
	public Boolean getIsTmall() {
		return isTmall;
	}
	public void setIsTmall(Boolean isTmall) {
		this.isTmall = isTmall;
	}
}
