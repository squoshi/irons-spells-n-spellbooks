package io.redspace.ironsspellbooks.entity.mobs;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMobRenderer;
import io.redspace.ironsspellbooks.util.DefaultBipedBoneIdents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HumanoidRenderer<T extends Mob & GeoAnimatable> extends GeoEntityRenderer<T> {
    private static final String LEFT_HAND = DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT;
    private static final String RIGHT_HAND = DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT;
    private static final String LEFT_BOOT = DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT;
    private static final String RIGHT_BOOT = DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT;
    private static final String LEFT_BOOT_2 = DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_2_IDENT;
    private static final String RIGHT_BOOT_2 = DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_2_IDENT;
    private static final String LEFT_ARMOR_LEG = DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT;
    private static final String RIGHT_ARMOR_LEG = DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT;
    private static final String LEFT_ARMOR_LEG_2 = DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_2_IDENT;
    private static final String RIGHT_ARMOR_LEG_2 = DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_2_IDENT;
    private static final String CHESTPLATE = DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT;
    private static final String RIGHT_SLEEVE = DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT;
    private static final String LEFT_SLEEVE = DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT;
    private static final String HELMET = DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT;

    public HumanoidRenderer(EntityRendererProvider.Context renderManager, GeoModel<T> model) {
        super(renderManager, model);
        addRenderLayer(new ItemArmorGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getArmorItemForBone(GeoBone bone, T animatable) {
                // Return the items relevant to the bones being rendered for additional rendering
                return switch (bone.getName()) {
                    case LEFT_BOOT, RIGHT_BOOT, LEFT_BOOT_2, RIGHT_BOOT_2 -> this.bootsStack;
                    case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG, LEFT_ARMOR_LEG_2, RIGHT_ARMOR_LEG_2 -> this.leggingsStack;
                    case CHESTPLATE, RIGHT_SLEEVE, LEFT_SLEEVE -> this.chestplateStack;
                    case HELMET -> this.helmetStack;
                    default -> null;
                };
            }

            // Return the equipment slot relevant to the bone we're using
            @Nonnull
            @Override
            protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, T animatable) {
                return switch (bone.getName()) {
                    case LEFT_BOOT, RIGHT_BOOT, LEFT_BOOT_2, RIGHT_BOOT_2 -> EquipmentSlot.FEET;
                    case LEFT_ARMOR_LEG, RIGHT_ARMOR_LEG, LEFT_ARMOR_LEG_2, RIGHT_ARMOR_LEG_2 -> EquipmentSlot.LEGS;
                    case RIGHT_SLEEVE -> !animatable.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                    case LEFT_SLEEVE -> animatable.isLeftHanded() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                    case CHESTPLATE -> EquipmentSlot.CHEST;
                    case HELMET -> EquipmentSlot.HEAD;
                    default -> super.getEquipmentSlotForBone(bone, stack, animatable);
                };
            }

            // Return the ModelPart responsible for the armor pieces we want to render
            @Nonnull
            @Override
            protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, T animatable, HumanoidModel<?> baseModel) {
                return switch (bone.getName()) {
                    case LEFT_BOOT, LEFT_BOOT_2, LEFT_ARMOR_LEG, LEFT_ARMOR_LEG_2 -> baseModel.leftLeg;
                    case RIGHT_BOOT, RIGHT_BOOT_2, RIGHT_ARMOR_LEG, RIGHT_ARMOR_LEG_2 -> baseModel.rightLeg;
                    case RIGHT_SLEEVE -> baseModel.rightArm;
                    case LEFT_SLEEVE -> baseModel.leftArm;
                    case CHESTPLATE -> baseModel.body;
                    case HELMET -> baseModel.head;
                    default -> super.getModelPartForBone(bone, slot, stack, animatable, baseModel);
                };
            }
        });

        // Add some held item rendering
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, T animatable) {
                if (animatable instanceof AbstractSpellCastingMob castingMob) {
                    if (castingMob.isDrinkingPotion()) {
                        if (castingMob.isLeftHanded() && bone.getName().equals(DefaultBipedBoneIdents.LEFT_HAND_BONE_IDENT) || !castingMob.isLeftHanded() && bone.getName().equals(DefaultBipedBoneIdents.RIGHT_HAND_BONE_IDENT))
                            return AbstractSpellCastingMobRenderer.makePotion(castingMob);
                    }
                }
                // Retrieve the items in the entity's hands for the relevant bone
                return switch (bone.getName()) {
                    case LEFT_HAND -> animatable.isLeftHanded() ?
                            animatable.getMainHandItem() : animatable.getOffhandItem();
                    case RIGHT_HAND -> animatable.isLeftHanded() ?
                            animatable.getOffhandItem() : animatable.getMainHandItem();
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
                // Apply the camera transform for the given hand
                return switch (bone.getName()) {
                    case LEFT_HAND, RIGHT_HAND -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    default -> ItemDisplayContext.NONE;
                };
            }

            // Do some quick render modifications depending on what the item is
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (stack.getItem() instanceof PotionItem) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                }
                if (stack == animatable.getMainHandItem()) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem)
                        poseStack.translate(0, 0.125, -0.25);
                } else if (stack == animatable.getOffhandItem()) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90f));

                    if (stack.getItem() instanceof ShieldItem) {
                        poseStack.translate(0, 0.125, 0.25);
                        poseStack.mulPose(Axis.YP.rotationDegrees(180));
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }
}