package io.improbable.keanu.e2e.regression;

import io.improbable.keanu.algorithms.variational.optimizer.gradient.GradientOptimizer;
import io.improbable.keanu.model.LinearRegression;
import io.improbable.keanu.network.BayesianNetwork;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.util.csv.ReadCsv;
import io.improbable.keanu.vertices.ConstantVertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This data set was taken from https://www4.stat.ncsu.edu/~boos/var.select/diabetes.html
 * It's also the same data set used for scikitlearn load_diabetes at
 * http://scikit-learn.org/stable/auto_examples/linear_model/plot_ols.html
 */
public class DiabetesLinearRegression {

    public static class Data {
        public DoubleTensor bmi;
        public DoubleTensor y;
    }

    @Test
    public void doesLinearRegressionOnBMI() {
        Data data = ReadCsv
            .fromResources("data/datasets/diabetes/diabetes_standardized_training.csv")
            .asVectorizedColumnsDefinedBy(Data.class)
            .load(true);

        // Linear Regression
        DoubleVertex weight = new GaussianVertex(0.0, 2.0);
        DoubleVertex b = new GaussianVertex(0.0, 2.0);
        DoubleVertex x = ConstantVertex.of(data.bmi);
        DoubleVertex yMu = x.multiply(weight);

        DoubleVertex y = new GaussianVertex(yMu.plus(b), 1.0);
        y.observe(data.y);

        BayesianNetwork bayesNet = new BayesianNetwork(y.getConnectedGraph());
        GradientOptimizer optimizer = GradientOptimizer.of(bayesNet);
        optimizer.maxLikelihood();

        assertEquals(938.2378, weight.getValue().scalar(), 0.01);
        assertEquals(152.9189, b.getValue().scalar(), 0.01);
    }

    @Test
    public void doesLinearRegressionOnBMIAsModel() {
        Data data = ReadCsv
            .fromResources("data/datasets/diabetes/diabetes_standardized_training.csv")
            .asVectorizedColumnsDefinedBy(Data.class)
            .load(true);

        LinearRegression regression = new LinearRegression(data.bmi, data.y, 0.0, 2.0, 100);
        regression.fit();
        assertEquals(938.2378, regression.getWeight(0), 0.5);
        assertEquals(152.9189, regression.getIntercept().getValue().scalar(), 0.5);
    }

    @Test
    public void canPredictFutureValuesWithLinearRegression() {
        Data data = ReadCsv
            .fromResources("data/datasets/diabetes/diabetes_standardized_training.csv")
            .asVectorizedColumnsDefinedBy(Data.class)
            .load(true);

        int sizeOfTestData = 100;

        List<DoubleTensor> splitXData = data.bmi.split(1, (int) data.bmi.getLength() - sizeOfTestData, (int) data.bmi.getLength() - 1);
        DoubleTensor xTrainingData = splitXData.get(0);
        DoubleTensor xTestData = splitXData.get(1);

        List<DoubleTensor> splitYData = data.y.split(1, (int) data.y.getLength() - sizeOfTestData, (int) data.bmi.getLength() - 1);
        DoubleTensor yTrainingData = splitYData.get(0);
        DoubleTensor yTestData = splitYData.get(1);

        LinearRegression regression = new LinearRegression(xTrainingData, yTrainingData);
        regression.fit();

        double accuracyOnTestData = regression.score(xTestData, yTestData);
        assertThat(accuracyOnTestData, both(greaterThan(0.)).and(lessThan(1.)));
    }

}
