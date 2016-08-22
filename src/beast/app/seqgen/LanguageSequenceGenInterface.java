package beast.app.seqgen;

import java.io.File;

import beast.app.util.Application;
import beast.core.Input;
import beast.core.Runnable;
import beast.core.Input.Validate;

public class LanguageSequenceGenInterface extends Runnable {
	File file = null;
	public Input<File> fileInput = new Input<>("input", "BEAST xml for sequence generation", Validate.REQUIRED);
	public Input<Integer> numberOfMeaningClassesInput = new Input<>("meaningClasses",
			"Number of meaning classes (default 1 [No classes])", 1, Validate.REQUIRED);
	public Input<File> fileOutput = new Input<>("output", "Name of output file");
	
	@Override
	public void initAndValidate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() throws Exception {
		String fileInputString = fileInput.get().getPath().replace("\\", "/");
		String meaningClasses = numberOfMeaningClassesInput.get().toString();
		String fileOutputString = fileOutput.get().getPath().replace("\\", "/");

		String[] args;
		
		if (fileOutputString.equals("") || fileOutputString.equals("[[none]]")) {
			args = new String[] {fileInputString, meaningClasses};
		} else {
			args = new String[] {fileInputString, meaningClasses, fileOutputString};
		}
		
		LanguageSequenceGen.main(args);

	}

	public static void main(String[] args) throws Exception {
		new Application(new LanguageSequenceGenInterface(), "Language Sequence Generation", args);
	}

}
