package model;

public interface SistemPembayaran {

	public abstract void topUp(int saldo);
	public abstract void transfer(int jumlahTransfer, Card card);

}
