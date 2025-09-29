import React, { useState } from "react";
import axios from "axios";
import { Typography, TextField, Button, Paper, Box } from "@mui/material";
import { useNavigate } from "react-router-dom";

const initial = {
  postId: "",
  postProfile: "",
  reqExperience: 0,
  postTechStack: [],
  postDesc: "",
};

const Create = () => {
  const skillSet = [
    { name: "Javascript" },
    { name: "Java" },
    { name: "Python" },
    { name: "Django" },
    { name: "Rust" },
  ];

  const navigate = useNavigate();
  const [form, setForm] = useState(initial);

  // handle form submit
  const handleSubmit = (e) => {
    e.preventDefault();
    axios
      .post("http://localhost:8080/jobPost", form)
      .then((resp) => {
        console.log("Job created:", resp.data);
        navigate("/"); // navigate only after success
      })
      .catch((error) => {
        console.error("Error creating job:", error);
      });
  };

  const { postId, postProfile, reqExperience, postDesc, postTechStack } = form;

  // handle checkbox changes
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

  return (
    <Paper sx={{ padding: "1%" }} elevation={0}>
      <Typography sx={{ margin: "3% auto" }} align="center" variant="h5">
        Create New Post
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
            value={postId}
          />
          <TextField
            type="text"
            sx={{ width: "50%", margin: "2% auto" }}
            required
            onChange={(e) => setForm({ ...form, postProfile: e.target.value })}
            label="Job Profile"
            variant="outlined"
            value={postProfile}
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
            value={reqExperience}
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
            value={postDesc}
          />
          <Box sx={{ margin: "1% auto" }}>
            <h3>Please mention required skills</h3>
            <ul style={{ listStyleType: "none", padding: 0 }}>
              {skillSet.map(({ name }, index) => (
                <li key={index}>
                  <input
                    type="checkbox"
                    id={`custom-checkbox-${index}`}
                    name={name}
                    value={name}
                    checked={postTechStack.includes(name)}
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
            Submit
          </Button>
        </Box>
      </form>
    </Paper>
  );
};

export default Create;
