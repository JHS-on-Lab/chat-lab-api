package me.son.chatlabapi.filebox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFileRequest(
        @NotBlank(message = "파일 제목은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "파일 제목은 100자 이하여야 합니다.") String title,
        @NotBlank(message = "확장자는 비어 있을 수 없습니다.")
        @Size(max = 20, message = "확장자는 20자 이하여야 합니다.") String extension,
        String content
) {
}
