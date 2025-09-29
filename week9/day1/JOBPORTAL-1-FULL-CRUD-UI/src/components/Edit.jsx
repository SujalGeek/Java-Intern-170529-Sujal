import React, { useEffect, useState } from "react";
import { Typography, TextField, Button, Paper, Box } from "@mui/material";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const initial = {
  postId: "",
  postProfile: "",
  reqExperience: 0,
  postTechStack: [],
  postDesc: "",
};

const Edit = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const [form, setForm] = useState(initial);
  const currId = location.state.id;

  useEffect(() => {
    axios
      .get(`http://localhost:8080/jobPost/${currId}`)
      .then((response) => {
        setForm(response.data);
      })
      .catch((error) => {
        console.error("Error fetching job:", error);
      });
  }, [currId]);

  const handleSubmit = (e) => {
    e.preventDefault();
    axios
      .put(`http://localhost:8080/jobPost/${currId}`, form) // ✅ PUT instead of POST
      .then((resp) => {
        console.log("Job updated:", resp.data);
        navigate("/"); // ✅ Navigate after success
      })
      .catch((error) => {
        console.error("Error updating job:", error);
      });
  };

  // ✅ Proper checkbox handler
  const handleCheckboxChange = (e) => {
    const { value, checked } = e.target;
    if (checked) {
      setForm({ ...form, postTechStack: [...form.postTechStack, value] });
    } else {
      setForm({
        ...form,
        postTechStack: form.postTechStack.filter((skill) => skill !== value),
      });
    }
  };

  const skillSet = ["Javascript", "Java", "Python", "Django", "Rust"];

  return (
    <Paper sx={{ padding: "1%" }} elevation={0}>
      <Typography sx={{ margin: "3% auto" }} align="center" variant="h5">
        Edit Post
      </Typography>
      <form autoComplete="off" noValidate onSubmit={handleSubmit}>
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            flexDirection: "column",
          }}
        >
          <TextField
            min="0"
            type="number"
            sx={{ width: "50%", margin: "2% auto" }}
            onChange={(e) => setForm({ ...form, postId: e.target.value })}
            label="Enter your Post ID"
            variant="outlined"
            value={form.postId}
          />
          <TextField
            type="text"
            sx={{ width: "50%", margin: "2% auto" }}
            required
            onChange={(e) => setForm({ ...form, postProfile: e.target.value })}
            label="Job Profile"
            variant="outlined"
            value={form.postProfile}
          />
          <TextField
            min="0"
            type="number"
            sx={{ width: "50%", margin: "2% auto" }}
            required
            onChange={(e) =>
              setForm({ ...form, reqExperience: e.target.value })
            }
            label="Years of Experience"
            variant="outlined"
            value={form.reqExperience}
          />
          <TextField
            type="text"
            sx={{ width: "50%", margin: "2% auto" }}
            required
            multiline
            rows={4}
            onChange={(e) => setForm({ ...form, postDesc: e.target.value })}
            label="Job Description"
            variant="outlined"
            value={form.postDesc}
          />
          <Box sx={{ margin: "1% auto" }}>
            <h3>Please mention required skills</h3>
            <ul style={{ listStyleType: "none", padding: 0 }}>
              {skillSet.map((name, index) => (
                <li key={index}>
                  <input
                    type="checkbox"
                    id={`custom-checkbox-${index}`}
                    name={name}
                    value={name}
                    checked={form.postTechStack.includes(name)} // ✅ pre-checked if exists
                    onChange={handleCheckboxChange}
                  />
                  <label
                    htmlFor={`custom-checkbox-${index}`}
                    style={{ marginLeft: "8px" }}
                  >
                    {name}
                  </label>
                </li>
              ))}
            </ul>
          </Box>
          <Button
            sx={{ width: "50%", margin: "2% auto" }}
            variant="contained"
            type="submit"
          >
            Update
          </Button>
        </Box>
      </form>
    </Paper>
  );
};

export default Edit;
