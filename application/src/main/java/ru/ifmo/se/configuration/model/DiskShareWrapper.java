package ru.ifmo.se.configuration.model;

import com.hierynomus.smbj.share.DiskShare;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiskShareWrapper {
    DiskShare diskShare;
}
