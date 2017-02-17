/*
 * Licensed to GraphHopper GmbH under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 *
 * GraphHopper GmbH licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.graphhopper.jsprit.core.problem.job;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.*;

public class ServiceTest {

    @Test
    public void whenTwoServicesHaveTheSameId_theirReferencesShouldBeUnEqual() {
        Service one = new Service.Builder("service").addSizeDimension(0, 10).setLocation(Location.newInstance("foo")).build();
        Service two = new Service.Builder("service").addSizeDimension(0, 10).setLocation(Location.newInstance("fo")).build();

        assertTrue(one != two);
    }

    @Test
    public void whenTwoServicesHaveTheSameId_theyShouldBeEqual() {
        Service one = new Service.Builder("service").addSizeDimension(0, 10).setLocation(Location.newInstance("foo")).build();
        Service two = new Service.Builder("service").addSizeDimension(0, 10).setLocation(Location.newInstance("fo")).build();

        assertTrue(one.equals(two));
    }

    @Test
    public void noName() {
        Set<Service> serviceSet = new HashSet<Service>();
        Service one = new Service.Builder("service").addSizeDimension(0, 10).setLocation(Location.newInstance("foo")).build();
        Service two = new Service.Builder("service").addSizeDimension(0, 10).setLocation(Location.newInstance("fo")).build();
        serviceSet.add(one);
        //		assertTrue(serviceSet.contains(two));
        serviceSet.remove(two);
        assertTrue(serviceSet.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenCapacityDimValueIsNegative_throwIllegalStateExpception() {
        @SuppressWarnings("unused")
        Service s = new Service.Builder("s").setLocation(Location.newInstance("foo")).addSizeDimension(0, -10).build();
    }

    @Test
    public void whenAddingTwoCapDimension_nuOfDimsShouldBeTwo() {
        Service one = new Service.Builder("s").setLocation(Location.newInstance("foofoo"))
                        .addSizeDimension(0, 2)
                        .addSizeDimension(1, 4)
                        .build();
        assertEquals(2, one.getActivity().getLoadChange().getNuOfDimensions());
    }

    @Test
    public void sizeAtStartAndEndShouldBeCorrect() {
        Service one = new Service.Builder("s").setLocation(Location.newInstance("foofoo"))
                        .addSizeDimension(0, 2)
                        .addSizeDimension(1, 4)
                        .build();
        assertTrue(one.getSizeAtEnd().equals(one.getActivity().getLoadChange()));
        assertTrue(one.getSizeAtStart().equals(SizeDimension.Builder.newInstance().addDimension(0, 0).addDimension(1, 0).build()));
    }

    @Test
    public void whenShipmentIsBuiltWithoutSpecifyingCapacity_itShouldHvCapWithOneDimAndDimValOfZero() {
        Service one = new Service.Builder("s").setLocation(Location.newInstance("foofoo"))
                        .build();
        assertEquals(1, one.getActivity().getLoadChange().getNuOfDimensions());
        assertEquals(0, one.getActivity().getLoadChange().get(0));
    }

    @Test
    public void whenShipmentIsBuiltWithConstructorWhereSizeIsSpecified_capacityShouldBeSetCorrectly() {
        Service one = new Service.Builder("s").addSizeDimension(0, 1).setLocation(Location.newInstance("foofoo"))
                        .build();
        assertEquals(1, one.getActivity().getLoadChange().getNuOfDimensions());
        assertEquals(1, one.getActivity().getLoadChange().get(0));
    }

    @Test
    public void whenCallingForNewInstanceOfBuilder_itShouldReturnBuilderCorrectly() {
        Service.Builder builder = new Service.Builder("s");
        assertNotNull(builder);
    }

    @Test
    public void whenSettingNoType_itShouldReturn_service() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc")).build();
        assertEquals("pickup", s.getType());
    }

    @Test
    public void whenSettingLocation_itShouldBeSetCorrectly() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc")).build();
        assertEquals("loc", s.getActivity().getLocation().getId());
        assertEquals("loc", s.getActivity().getLocation().getId());
    }

    @Test
    public void whenSettingLocation_itShouldWork() {
        Service s = new Service.Builder("s").setLocation(Location.Builder.newInstance().setId("loc").build()).build();
        assertEquals("loc", s.getActivity().getLocation().getId());
        assertEquals("loc", s.getActivity().getLocation().getId());
    }


    @Test
    public void whenSettingLocationCoord_itShouldBeSetCorrectly() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance(1, 2)).build();
        assertEquals(1.0, s.getActivity().getLocation().getCoordinate().getX(), 0.01);
        assertEquals(2.0, s.getActivity().getLocation().getCoordinate().getY(), 0.01);
        assertEquals(1.0, s.getActivity().getLocation().getCoordinate().getX(), 0.01);
        assertEquals(2.0, s.getActivity().getLocation().getCoordinate().getY(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSettingNeitherLocationIdNorCoord_throwsException() {
        @SuppressWarnings("unused")
        Service s = new Service.Builder("s").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenServiceTimeSmallerZero_throwIllegalStateException() {
        @SuppressWarnings("unused")
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc")).setServiceTime(-1).build();
    }

    @Test
    public void whenSettingServiceTime_itShouldBeSetCorrectly() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc")).setServiceTime(1).build();
        assertEquals(1.0, s.getActivity().getOperationTime(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenTimeWindowIsNull_throwException() {
        @SuppressWarnings("unused")
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc")).setTimeWindow(null).build();
    }

    @Test
    public void whenSettingTimeWindow_itShouldBeSetCorrectly() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc")).setTimeWindow(TimeWindow.newInstance(1.0, 2.0)).build();
        assertEquals(1.0, s.getActivity().getSingleTimeWindow().getStart(), 0.01);
        assertEquals(2.0, s.getActivity().getSingleTimeWindow().getEnd(), 0.01);
    }

    @Test
    public void whenAddingSkills_theyShouldBeAddedCorrectly() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .addRequiredSkill("drill").addRequiredSkill("screwdriver").build();
        assertTrue(s.getRequiredSkills().containsSkill("drill"));
        assertTrue(s.getRequiredSkills().containsSkill("drill"));
        assertTrue(s.getRequiredSkills().containsSkill("ScrewDriver"));
    }

    @Test
    public void whenAddingSkillsCaseSens_theyShouldBeAddedCorrectly() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .addRequiredSkill("DriLl").addRequiredSkill("screwDriver").build();
        assertTrue(s.getRequiredSkills().containsSkill("drill"));
        assertTrue(s.getRequiredSkills().containsSkill("drilL"));
    }

    @Test
    public void whenAddingSeveralTimeWindows_itShouldBeSetCorrectly() {
        TimeWindow tw1 = TimeWindow.newInstance(1.0, 2.0);
        TimeWindow tw2 = TimeWindow.newInstance(3.0, 5.0);
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .addTimeWindow(tw1)
                        .addTimeWindow(tw2)
                        .build();
        assertEquals(2, s.getActivity().getTimeWindows().size());
        assertThat(s.getActivity().getTimeWindows(), hasItem(is(tw1)));
        assertThat(s.getActivity().getTimeWindows(), hasItem(is(tw2)));
    }

    @Test
    public void whenAddingTimeWindow_itShouldBeSetCorrectly() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .addTimeWindow(TimeWindow.newInstance(1.0, 2.0)).build();
        assertEquals(1.0, s.getActivity().getSingleTimeWindow().getStart(), 0.01);
        assertEquals(2.0, s.getActivity().getSingleTimeWindow().getEnd(), 0.01);
    }


    @Test
    public void whenAddingSkillsCaseSensV2_theyShouldBeAddedCorrectly() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .addRequiredSkill("screwDriver").build();
        assertFalse(s.getRequiredSkills().containsSkill("drill"));
        assertFalse(s.getRequiredSkills().containsSkill("drilL"));
    }

    @Test
    public void nameShouldBeAssigned() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .setName("name").build();
        assertEquals("name", s.getName());
    }

    @Test
    public void shouldKnowMultipleTimeWindows() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .addTimeWindow(TimeWindow.newInstance(0., 10.)).addTimeWindow(TimeWindow.newInstance(20., 30.))
                        .setName("name").build();
        assertEquals(2, s.getActivity().getTimeWindows().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenMultipleTWOverlap_throwEx() {
        new Service.Builder("s").setLocation(Location.newInstance("loc"))
        .addTimeWindow(TimeWindow.newInstance(0., 10.))
        .addTimeWindow(TimeWindow.newInstance(5., 30.))
        .setName("name").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenMultipleTWOverlap2_throwEx() {
        new Service.Builder("s").setLocation(Location.newInstance("loc"))
        .addTimeWindow(TimeWindow.newInstance(20., 30.))
        .addTimeWindow(TimeWindow.newInstance(0., 25.))
        .setName("name").build();
    }

    @Test
    public void whenSettingPriorities_itShouldBeSetCorrectly() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .setPriority(1).build();
        assertEquals(1, s.getPriority());
    }

    @Test
    public void whenSettingPriorities_itShouldBeSetCorrectly2() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .setPriority(3).build();
        assertEquals(3, s.getPriority());
    }

    @Test
    public void whenSettingPriorities_itShouldBeSetCorrectly3() {
        Service s = Service.Builder.newInstance("s").setLocation(Location.newInstance("loc"))
            .setPriority(10).build();
        assertEquals(10, s.getPriority());
    }

    @Test
    public void whenNotSettingPriorities_defaultShouldBe2() {
        Service s = new Service.Builder("s").setLocation(Location.newInstance("loc"))
                        .build();
        assertEquals(2, s.getPriority());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSettingIncorrectPriorities_itShouldThrowException() {
        new Service.Builder("s").setLocation(Location.newInstance("loc"))
        .setPriority(30).build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSettingIncorrectPriorities_itShouldThrowException2() {
        new Service.Builder("s").setLocation(Location.newInstance("loc"))
        .setPriority(0).build();

    }

}
