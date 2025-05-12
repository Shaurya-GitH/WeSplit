package com.wesplit.main.payloads;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupDTO {
    private Long groupId;
    private String groupName;
    private LocalDate createdAt;
    private List<FriendDTO> members;
}
