package com.dsatab.data.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Position {

	Head_Up("Kopf Oben"), Head_Side("Kopf seitl."), Head_Face("Gesicht"), Neck("Hals"), LeftShoulder("Linke Schulter",
			"Schulter L"), LeftUpperArm("Linker Oberarm", "Oberarm L"), LeftLowerArm("Linker Unterarm", "Unterarm L"), RightShoulder(
			"Rechte Schulter", "Schulter R"), RightUpperArm("Rechter Oberarm", "Oberarm R"), RightLowerArm(
			"Rechter Unterarm", "Unterarm R"), Brust("Brust"), Bauch("Bauch"), Ruecken("Rücken"), Pelvis("Becken"), UpperLeg(
			"Oberschenkel"), LowerLeg("Unterschenkel"), Kopf("Kopf"), LinkeHand("Linke Hand", "Hand L"), RechteHand(
			"Rechte Hand", "Hand R"), LinkesBein("Linkes Bein", "Bein L"), RechtesBein("Rechtes Bein", "Bein R");

	protected String name;
	protected String nameShort;

	private Position(String name, String nameShort) {
		this.name = name;
		this.nameShort = nameShort;
	}

	private Position(String name) {
		this.name = name;
		this.nameShort = name;
	}

	public String getName() {
		return name;
	}

	public String getNameSort() {
		return nameShort;
	}

	@Override
	public String toString() {
		return getName();
	}

	public static List<Position> WOUND_POSITIONS = new ArrayList<Position>(Arrays.asList(Position.Kopf, Position.Bauch,
			Position.Brust, Position.LeftLowerArm, Position.RightLowerArm, Position.LowerLeg, Position.UpperLeg));

	public static final Position[] messer_dolch_stich = new Position[21];
	static {
		messer_dolch_stich[1] = Position.UpperLeg;
		messer_dolch_stich[2] = Position.UpperLeg;
		messer_dolch_stich[3] = Position.RightShoulder;
		messer_dolch_stich[4] = Position.RightLowerArm;
		messer_dolch_stich[5] = Position.RightLowerArm;
		messer_dolch_stich[6] = Position.RechteHand;
		messer_dolch_stich[7] = Position.RechteHand;
		messer_dolch_stich[8] = Position.RightUpperArm;
		messer_dolch_stich[9] = Position.LeftLowerArm;
		messer_dolch_stich[10] = Position.LeftLowerArm;
		messer_dolch_stich[11] = Position.LinkeHand;
		messer_dolch_stich[12] = Position.Bauch;
		messer_dolch_stich[13] = Position.Bauch;
		messer_dolch_stich[14] = Position.Brust;
		messer_dolch_stich[15] = Position.Brust;
		messer_dolch_stich[16] = Position.Pelvis;
		messer_dolch_stich[17] = Position.LowerLeg;
		messer_dolch_stich[18] = Position.Head_Side;
		messer_dolch_stich[19] = Position.Head_Face;
		messer_dolch_stich[20] = Position.Head_Face;
	}

	public static final Position[] hieb_ketten = new Position[21];
	static {
		hieb_ketten[1] = Position.Pelvis;
		hieb_ketten[2] = Position.UpperLeg;
		hieb_ketten[3] = Position.RechteHand;
		hieb_ketten[4] = Position.RightLowerArm;
		hieb_ketten[5] = Position.RightLowerArm;
		hieb_ketten[6] = Position.LeftLowerArm;
		hieb_ketten[7] = Position.LeftLowerArm;
		hieb_ketten[8] = Position.RightUpperArm;
		hieb_ketten[9] = Position.RightUpperArm;
		hieb_ketten[10] = Position.LeftUpperArm;
		hieb_ketten[11] = Position.LeftUpperArm;
		hieb_ketten[12] = Position.Brust;
		hieb_ketten[13] = Position.LeftShoulder;
		hieb_ketten[14] = Position.RightShoulder;
		hieb_ketten[15] = Position.RightShoulder;
		hieb_ketten[16] = Position.Bauch;
		hieb_ketten[17] = Position.Head_Up;
		hieb_ketten[18] = Position.Head_Up;
		hieb_ketten[19] = Position.LowerLeg;
		hieb_ketten[20] = Position.Head_Side;
	}

	public static final Position[] schwert_saebel = new Position[21];
	static {
		schwert_saebel[1] = Position.RechteHand;
		schwert_saebel[2] = Position.RightUpperArm;
		schwert_saebel[3] = Position.RightLowerArm;
		schwert_saebel[4] = Position.RightLowerArm;
		schwert_saebel[5] = Position.UpperLeg;
		schwert_saebel[6] = Position.UpperLeg;
		schwert_saebel[7] = Position.Pelvis;
		schwert_saebel[8] = Position.Pelvis;
		schwert_saebel[9] = Position.Bauch;
		schwert_saebel[10] = Position.Bauch;
		schwert_saebel[11] = Position.LowerLeg;
		schwert_saebel[12] = Position.LeftShoulder;
		schwert_saebel[13] = Position.RightShoulder;
		schwert_saebel[14] = Position.LeftUpperArm;
		schwert_saebel[15] = Position.LeftLowerArm;
		schwert_saebel[16] = Position.Brust;
		schwert_saebel[17] = Position.Brust;
		schwert_saebel[18] = Position.Head_Face;
		schwert_saebel[19] = Position.Head_Up;
		schwert_saebel[20] = Position.Head_Side;
	}

	public static final Position[] stangen_zweih_stich = new Position[21];
	static {
		stangen_zweih_stich[1] = Position.LeftLowerArm;
		stangen_zweih_stich[2] = Position.LeftUpperArm;
		stangen_zweih_stich[3] = Position.LeftUpperArm;
		stangen_zweih_stich[4] = Position.RightLowerArm;
		stangen_zweih_stich[5] = Position.RightUpperArm;
		stangen_zweih_stich[6] = Position.UpperLeg;
		stangen_zweih_stich[7] = Position.UpperLeg;
		stangen_zweih_stich[8] = Position.RightShoulder;
		stangen_zweih_stich[9] = Position.LowerLeg;
		stangen_zweih_stich[10] = Position.Bauch;
		stangen_zweih_stich[11] = Position.Bauch;
		stangen_zweih_stich[12] = Position.Bauch;
		stangen_zweih_stich[13] = Position.RechteHand;
		stangen_zweih_stich[14] = Position.Brust;
		stangen_zweih_stich[15] = Position.Brust;
		stangen_zweih_stich[16] = Position.Brust;
		stangen_zweih_stich[17] = Position.Neck;
		stangen_zweih_stich[18] = Position.Pelvis;
		stangen_zweih_stich[19] = Position.Head_Face;
		stangen_zweih_stich[20] = Position.Head_Face;
	}

	public static final Position[] stangen_zweih_hieb = new Position[21];
	static {
		stangen_zweih_hieb[1] = Position.UpperLeg;
		stangen_zweih_hieb[2] = Position.UpperLeg;
		stangen_zweih_hieb[3] = Position.LowerLeg;
		stangen_zweih_hieb[4] = Position.Bauch;
		stangen_zweih_hieb[5] = Position.Pelvis;
		stangen_zweih_hieb[6] = Position.RightLowerArm;
		stangen_zweih_hieb[7] = Position.RightLowerArm;
		stangen_zweih_hieb[8] = Position.RightUpperArm;
		stangen_zweih_hieb[9] = Position.LeftLowerArm;
		stangen_zweih_hieb[10] = Position.RechteHand;
		stangen_zweih_hieb[11] = Position.LeftUpperArm;
		stangen_zweih_hieb[12] = Position.LeftShoulder;
		stangen_zweih_hieb[13] = Position.RightShoulder;
		stangen_zweih_hieb[14] = Position.RightShoulder;
		stangen_zweih_hieb[15] = Position.Brust;
		stangen_zweih_hieb[16] = Position.Brust;
		stangen_zweih_hieb[17] = Position.Head_Up;
		stangen_zweih_hieb[18] = Position.Head_Up;
		stangen_zweih_hieb[19] = Position.Head_Side;
		stangen_zweih_hieb[20] = Position.Head_Side;
	}

	public static final Position[] box_rauf_hruru = new Position[21];
	static {
		box_rauf_hruru[1] = Position.Brust;
		box_rauf_hruru[2] = Position.Brust;
		box_rauf_hruru[3] = Position.LeftUpperArm;
		box_rauf_hruru[4] = Position.RightUpperArm;
		box_rauf_hruru[5] = Position.LeftLowerArm;
		box_rauf_hruru[6] = Position.LeftLowerArm;
		box_rauf_hruru[7] = Position.RightLowerArm;
		box_rauf_hruru[8] = Position.RightLowerArm;
		box_rauf_hruru[9] = Position.RechteHand;
		box_rauf_hruru[10] = Position.Head_Face;
		box_rauf_hruru[11] = Position.Head_Face;
		box_rauf_hruru[12] = Position.Head_Face;
		box_rauf_hruru[13] = Position.Bauch;
		box_rauf_hruru[14] = Position.Bauch;
		box_rauf_hruru[15] = Position.Pelvis;
		box_rauf_hruru[16] = Position.LeftShoulder;
		box_rauf_hruru[17] = Position.RightShoulder;
		box_rauf_hruru[18] = Position.Head_Side;
		box_rauf_hruru[19] = Position.Head_Side;
		box_rauf_hruru[20] = Position.Head_Side;
	}

	public static final Position[] fern_wurf = new Position[21];
	static {
		fern_wurf[1] = Position.LeftShoulder;
		fern_wurf[2] = Position.RightShoulder;
		fern_wurf[3] = Position.Pelvis;
		fern_wurf[4] = Position.Pelvis;
		fern_wurf[5] = Position.LeftUpperArm;
		fern_wurf[6] = Position.RightUpperArm;
		fern_wurf[7] = Position.LeftLowerArm;
		fern_wurf[8] = Position.RightLowerArm;
		fern_wurf[9] = Position.Brust;
		fern_wurf[10] = Position.Brust;
		fern_wurf[11] = Position.Brust;
		fern_wurf[12] = Position.Neck;
		fern_wurf[13] = Position.Bauch;
		fern_wurf[14] = Position.Bauch;
		fern_wurf[15] = Position.RechteHand;
		fern_wurf[16] = Position.LowerLeg;
		fern_wurf[17] = Position.UpperLeg;
		fern_wurf[18] = Position.UpperLeg;
		fern_wurf[19] = Position.Kopf;
		fern_wurf[20] = Position.Kopf;
	}

	public static final Position[] official = new Position[21];
	static {
		official[1] = Position.LinkesBein;
		official[2] = Position.RechtesBein;
		official[3] = Position.LinkesBein;
		official[4] = Position.RechtesBein;
		official[5] = Position.LinkesBein;
		official[6] = Position.RechtesBein;
		official[7] = Position.Bauch;
		official[8] = Position.Bauch;
		official[9] = Position.LinkeHand;
		official[10] = Position.RechteHand;
		official[11] = Position.LinkeHand;
		official[12] = Position.RechteHand;
		official[13] = Position.LinkeHand;
		official[14] = Position.RechteHand;
		official[15] = Position.Brust;
		official[16] = Position.Brust;
		official[17] = Position.Brust;
		official[18] = Position.Brust;
		official[19] = Position.Kopf;
		official[20] = Position.Kopf;
	}
}
