package net.runelite.launcher;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.runelite.launcher.beans.Artifact;

@Data
@NoArgsConstructor
public class BootstrapDependencies {
    Artifact[] artifacts;
    String hash;
    String errorMessage;
    String hookHash;
}
