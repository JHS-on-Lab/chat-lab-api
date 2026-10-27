package me.son.chatlabapi.filebox.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFolderRequest(
        @NotBlank(message = "폴더 이름은 비어 있을 수 없습니다.")
        @Size(max = 100, message = "폴더 이름은 100자 이하여야 합니다.") String name,
        Long parentId
) {
}
