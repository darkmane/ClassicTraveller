package com.ffe.traveller.classic.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ffe.traveller.classic.views.UniversalPlanetaryProfile;
import com.ffe.traveller.classic.views.Starport;

import static com.ffe.traveller.classic.views.UniversalPlanetaryProfileMaker.CreateUniversalPlanetaryProfile;

public class UPPTests {

	@Test
	public void sizeTest() {
		UniversalPlanetaryProfile upp = CreateUniversalPlanetaryProfile(
                Starport.none, 1, 0, 0, 0, 0, 0);
		String rv = upp.Size();
		Assert.assertEquals("1000 miles (1600 km)",rv);
	}
}