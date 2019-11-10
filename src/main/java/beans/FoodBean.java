package beans;

public class FoodBean {
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public float getQuantity() {
		return quantity;
	}
	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}
	public String getValid_start_date() {
		return valid_start_date;
	}
	public void setValid_start_date(String valid_start_date) {
		this.valid_start_date = valid_start_date;
	}
	public String getValid_end_date() {
		return valid_end_date;
	}
	public void setValid_end_date(String valid_end_date) {
		this.valid_end_date = valid_end_date;
	}
	
	String type;
	float quantity;
	String valid_start_date; 
	String valid_end_date;
}
