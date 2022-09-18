package com.example.demo.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UploadFileList {

	List<UploadFile> uploadFileList;

	@Builder
	public UploadFileList(List<UploadFile> uploadFileList) {
		this.uploadFileList = uploadFileList;
	}


}
