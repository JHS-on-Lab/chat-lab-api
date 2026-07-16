package me.son.chatlabapi.filebox.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFolderRequest(
        @NotBlank(message = "폴더 이름은 비어 있을 수 없습니다.") String name
) {
}
