package me.son.chatlabapi.filebox.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFileRequest(
        @NotBlank(message = "파일 제목은 비어 있을 수 없습니다.") String title,
        @NotBlank(message = "확장자는 비어 있을 수 없습니다.") String extension,
        String content
) {
}
