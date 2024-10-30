package cy.jdkdigital.productivefarming.util;

import net.minecraft.core.BlockPos;

public record FarmConfig(String type, BlockPos firstCorner, BlockPos secondCorner) {}
