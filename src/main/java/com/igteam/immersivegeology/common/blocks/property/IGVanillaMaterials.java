package com.igteam.immersivegeology.common.blocks.property;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;

public final class IGVanillaMaterials {
    public static final Material MOSS;

    static {
        MOSS = new Builder(MaterialColor.SNOW).doesNotBlockMovement().notOpaque().notSolid().pushDestroys().replaceable().requiresTool().build();
    }

    public static class Builder {
        private PushReaction pushReaction;
        private boolean blocksMovement;
        private boolean canBurn;
        private boolean requiresNoTool;
        private boolean isLiquid;
        private boolean isReplaceable;
        private boolean isSolid;
        private final MaterialColor color;
        private boolean isOpaque;

        public Builder(MaterialColor p_i48270_1_) {
            this.pushReaction = PushReaction.NORMAL;
            this.blocksMovement = true;
            this.requiresNoTool = true;
            this.isSolid = true;
            this.isOpaque = true;
            this.color = p_i48270_1_;
        }

        public Builder liquid() {
            this.isLiquid = true;
            return this;
        }

        public Builder notSolid() {
            this.isSolid = false;
            return this;
        }

        public Builder doesNotBlockMovement() {
            this.blocksMovement = false;
            return this;
        }

        private Builder notOpaque() {
            this.isOpaque = false;
            return this;
        }

        protected Builder requiresTool() {
            this.requiresNoTool = false;
            return this;
        }

        protected Builder flammable() {
            this.canBurn = true;
            return this;
        }

        public Builder replaceable() {
            this.isReplaceable = true;
            return this;
        }

        protected Builder pushDestroys() {
            this.pushReaction = PushReaction.DESTROY;
            return this;
        }

        protected Builder pushBlocks() {
            this.pushReaction = PushReaction.BLOCK;
            return this;
        }

        public Material build() {
            return new Material(this.color, this.isLiquid, this.isSolid, this.blocksMovement, this.isOpaque, this.requiresNoTool, this.canBurn, this.isReplaceable, this.pushReaction);
        }
    }
}
