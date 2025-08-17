package com.example.Open_Position_Hub.fetch;

import java.util.List;

public record FetchProfile(
    boolean dynamic,
    List<DropdownRecipe> dropdowns
) {
    public static FetchProfile greetingV2() {
        return new FetchProfile(
            true,
            List.of(new DropdownRecipe(
                "span.sc-86b147bc-0.ghZIoe",
                "#dropdown-portal",
                "#dropdown-portal"
            ))
        );

    }

    public static FetchProfile staticPage() {
        return new FetchProfile(false, null);
    }
}
