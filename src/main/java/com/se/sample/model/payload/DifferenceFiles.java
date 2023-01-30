package com.se.sample.model.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifferenceFiles {
    private  String diffLeft;
    private  String diffRight;
}
