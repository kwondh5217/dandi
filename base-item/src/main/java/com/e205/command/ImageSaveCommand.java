package com.e205.command;

import com.e205.ImageType;
import org.springframework.core.io.Resource;

public record ImageSaveCommand(
    Resource image,
    ImageType type
) {

}
