package com.deco.helper;

public class Helper {
	static public double genCorrectScore(int nHandicap, int nHomeBack, int nAwayBack, int nHomeGoals, int nAwayGoals)
	{
		double[] lsBack = new double[] {};
		double nMagic = 0.05;
		int nOdd = (nHomeGoals - nAwayGoals);
		int nAbs = Math.abs(nOdd);
		if (nAbs == 0)
			lsBack = new double[] {9, 5.5, 16, 30, 50};
		else if (nAbs == 1)
			lsBack = new double[] {7, 9, 28};
		else if (nAbs == 2)
			lsBack = new double[] {11, 18, 80};
		else if (nAbs == 3)
			lsBack = new double[] {23, 53};
		else if (nAbs == 4)
			lsBack = new double[] {35};
			
		int nIndex = 0;
		if (nHomeGoals > nAwayGoals)
			nIndex = nAwayGoals;
		else
			nIndex = nHomeGoals;
		
		// Money Back
		double nMoneyBack = lsBack[nIndex];
		
		// Calculate Percent 1
		double nHalfBack = (nHomeBack + nAwayBack) / 2;
		double nPercent = (nHomeBack - nHalfBack) / nHalfBack;
		double nPlus1 = nMoneyBack * nPercent;
		
		// Calculate Percent 2
		nPercent = Math.abs(nHandicap * nMagic);
		double nPlus2 = nMoneyBack * nPercent;	
		
		if (nHandicap * nOdd > 0)
			nMoneyBack += nPlus2 * 1.5 * (Math.abs(nOdd) + 1);
		else if (nHandicap * nOdd < 0)
			nMoneyBack -= nPlus2;
		else
			nMoneyBack += Math.abs(nPlus2) + (Math.abs(nHandicap) * 1.5);
			
		nMoneyBack += nPlus1;		
		
		if (nHandicap * nOdd < 0 && Math.abs(nHandicap) > Math.abs(nOdd * 4)){
			nMoneyBack = lsBack[nIndex] + (Math.abs(nHandicap) - Math.abs(nOdd * 4)) * (0.6);
		}				

		
		return Math.round(nMoneyBack);
	}
	
	
	static public double genMatchResult(int nHandicap, int nHomeBack, int nAwayBack, int nWin){
		int[] lsMagic = new int[] {};
		double nMagic = 0;;
		double nMagic2 = 0;
		
		int nIndex = Math.abs(nHandicap);
		int nBase = 250;
		int nOdd = nHomeBack - nAwayBack;
		double nAbsHandicap = Math.abs(nHandicap);
		
		if (nWin == 0){
			if (nHandicap <= 0){
				lsMagic = new int[] {0, -40, -60, -75, -90, -110, -125, -135, -138};
				nMagic = -1.2;	
				if (nAbsHandicap == 0)
					nAbsHandicap = 0.9;
				nMagic2 = 5 * Math.abs(nOdd / 2) / nAbsHandicap * 0.6;
				if (nHomeBack < nAwayBack)
					nMagic2 = -nMagic2;
			}
			else{
				lsMagic = new int[] {0, 40, 100, 160, 220, 290, 380, 460, 500};
				nMagic = 120;	
				nMagic2 = 5 * Math.abs(nOdd / 2) * nAbsHandicap;
				if (nHomeBack < nAwayBack)
					nMagic2 = -nMagic2 / 1.8;		
			}
		}
		else if (nWin == 1){
			if (nHandicap >= 0){
				lsMagic = new int[] {0, -40, -60, -75, -90, -110, -125, -135, -138};
				nMagic = -1.2;	
				if (nAbsHandicap == 0)
					nAbsHandicap = 0.9;			
				nMagic2 = 5 * Math.abs(nOdd / 2) / nAbsHandicap * 0.6;
				if (nHomeBack > nAwayBack)
					nMagic2 = -nMagic2;
			}
			else{
				lsMagic = new int[] {0, 40, 100, 160, 220, 290, 380, 460, 500};
				nMagic = 120;	
				nMagic2 = 5 * Math.abs(nOdd / 2) * nAbsHandicap;
				if (nHomeBack > nAwayBack)
					nMagic2 = -nMagic2 / 1.8;		
			}	
		}
		else{
			lsMagic = new int[] {0, 30, 50, 70, 100, 140, 190, 250, 320};
			nBase = 300;	
			if (nHandicap < 0){
				if (nHomeBack < nAwayBack){
					nMagic = 100;	
					nMagic2 = 5 * Math.abs(nOdd / 2) * nAbsHandicap / 1.6;
				}
				else{
					nMagic = 30;	
					nMagic2 = 5 * Math.abs(nOdd / 2) / nAbsHandicap;
					nMagic2 = -nMagic2 / 1.6;
				}
			}
			else if (nHandicap > 0){
				if (nHomeBack > nAwayBack){
					nMagic = 100;	
					nMagic2 = 5 * Math.abs(nOdd / 2) * nAbsHandicap / 1.6;
				}
				else{
					nMagic = 30;
					nMagic2 = 5 * Math.abs(nOdd / 2) / nAbsHandicap;
					nMagic2 = -nMagic2 / 1.6;
				}	
			}
			else{
				nMagic2 = 5 * Math.abs(nOdd / 2) / 0.5;		
			}
		}
		
		double nBack;
		if (nIndex < 9){
			nBack = nBase + lsMagic[nIndex];
		}
		else{
			nBack = nBase + lsMagic[8] + (nIndex - 8) * nMagic;
		}

		return Math.round(nBack + nMagic2) / 100;	
	}	
}
