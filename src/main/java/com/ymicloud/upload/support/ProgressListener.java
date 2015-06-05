package com.ymicloud.upload.support;

public abstract class ProgressListener {

	private boolean canceled = false;

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public abstract void update(long total, long progress);
}
