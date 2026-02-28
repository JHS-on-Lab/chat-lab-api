package me.son.chatlabapi.global.paging.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageRequestDto {
    private int page = 0;
    private int size = 10;
}
