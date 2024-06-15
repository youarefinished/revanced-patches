package app.revanced.patches.youtube.utils.fix.client.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.annotation.FuzzyPatternScanMethod
import app.revanced.patcher.fingerprint.MethodFingerprint
import app.revanced.patches.youtube.utils.fix.client.fingerprints.PlayerGestureConfigSyntheticFingerprint.indexOfDownAndOutAllowedInstruction
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/**
 * Annotation [FuzzyPatternScanMethod] is required to maintain compatibility with YouTube v18.29.38 ~ v18.32.39.
 *
 * TODO: Remove this annotation if support for YouTube v18.29.38 to v18.32.39 is dropped.
 */
@FuzzyPatternScanMethod(5)
internal object PlayerGestureConfigSyntheticFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Ljava/lang/Object;"),
    opcodes = listOf(
        Opcode.SGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_EQZ,
        Opcode.IF_EQZ,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,      // playerGestureConfig.downAndOutLandscapeAllowed
        Opcode.MOVE_RESULT,
        Opcode.CHECK_CAST,
        Opcode.IPUT_BOOLEAN,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,      // playerGestureConfig.downAndOutPortraitAllowed
        Opcode.MOVE_RESULT,
        Opcode.IPUT_BOOLEAN,
        Opcode.RETURN_VOID,
    ),
    customFingerprint = { methodDef, classDef ->
        indexOfDownAndOutAllowedInstruction(methodDef) > 0 &&
        // This method is always called "a" because this kind of class always has a single method.
        methodDef.name == "a" && classDef.methods.count() == 2
    }
) {
    fun indexOfDownAndOutAllowedInstruction(methodDef: Method) =
        methodDef.indexOfFirstInstruction {
            val reference = getReference<MethodReference>()
            reference?.definingClass == "Lcom/google/android/libraries/youtube/innertube/model/media/PlayerConfigModel;" &&
                    reference.parameterTypes.isEmpty() &&
                    reference.returnType == "Z"
        }
}