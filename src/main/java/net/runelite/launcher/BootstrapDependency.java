package net.runelite.launcher;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.runelite.launcher.beans.Artifact;

@Data
@NoArgsConstructor
public class BootstrapDependency {
    Artifact[] artifacts;
    String hash;
    String errorMessage;
    String hookHash;
}
