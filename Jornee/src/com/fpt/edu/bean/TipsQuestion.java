/*******************************************************************************
 * Capstone Project: Jornee 
 * FileName: TipsQuestion.java
 * Copyright © 2014 4T1C - aka Group 3 - FPT University Da Nang. 
 * All rights reserved. 
 * No part of this application source code or any of its contents may be reproduced, 
 * copied, modified or adapted, without the prior written consent of the author, 
 * unless otherwise indicated for stand-alone materials.
 ******************************************************************************/

package com.fpt.edu.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class TipsQuestion implements Parcelable {
	private String questionId;
	private String content;
	private String modifiedDate;
	private String answer;

	public TipsQuestion(String content) {

		this.content = content;
	}

	public TipsQuestion() {

	}
	public TipsQuestion(String questionId, String content, String modifiedDate) {
		super();
		this.questionId = questionId;
		this.content = content;
		this.modifiedDate = modifiedDate;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getQuestionId() {
		return questionId;
	}

	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// Parcelling part
	public TipsQuestion(Parcel in) {
		String[] data = new String[3];

		in.readStringArray(data);
		this.questionId = data[0];
		this.content = data[1];
		this.modifiedDate = data[2];
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.questionId, this.content,
				this.modifiedDate });
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public TipsQuestion createFromParcel(Parcel in) {
			return new TipsQuestion(in);
		}

		public TipsQuestion[] newArray(int size) {
			return new TipsQuestion[size];
		}
	};
}
