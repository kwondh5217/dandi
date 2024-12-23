package com.e205.base.item.command;

import com.e205.base.item.ImageType;
import org.springframework.core.io.Resource;

public record ImageSaveCommand(
    Resource image,
    ImageType type
) {

}
