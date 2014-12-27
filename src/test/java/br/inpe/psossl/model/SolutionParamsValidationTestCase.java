package br.inpe.psossl.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class SolutionParamsValidationTestCase {

    @Test
    public void mustAcceptCorrectParams() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, 1, 1));
        Solution solution = new Solution(new Container(1,1), items);
        assertTrue(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfNullItemList() {
        Solution solution = new Solution(new Container(1,1), null);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfEmptyItemList() {
        Solution solution = new Solution(new Container(1,1), new ArrayList<Equipment>());
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveNullContainer() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, 1, 1));
        Solution solution = new Solution(null, items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveZeroWidthContainer() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, 1, 1));
        Solution solution = new Solution(new Container(0,1), items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveZeroHeightContainer() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, 1, 1));
        Solution solution = new Solution(new Container(1,0), items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveNegativeWidthContainer() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, 1, 1));
        Solution solution = new Solution(new Container(-1,1), items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveNegativeHeightContainer() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, 1, 1));
        Solution solution = new Solution(new Container(1,-1), items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveItemWithZeroWidth() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(0, 1, 1));
        Solution solution = new Solution(new Container(1,1), items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveItemWithZeroHight() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, 0, 1));
        Solution solution = new Solution(new Container(1,1), items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveItemWithNegativeWidth() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(-1, 1, 1));
        Solution solution = new Solution(new Container(1,1), items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveItemWithNegativeHeight() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, -1, 1));
        Solution solution = new Solution(new Container(1,1), items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveItemWithZeroMass() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, 1, 0));
        Solution solution = new Solution(new Container(1,1), items);
        assertFalse(solution.validateParams());
    }

    @Test
    public void mustFailParamsValidationIfHaveItemWithNegativeMass() {
        List<Equipment> items = new ArrayList<Equipment>();
        items.add(new Equipment(1, 1, -1));
        Solution solution = new Solution(new Container(1,1), items);
        assertFalse(solution.validateParams());
    }

}
